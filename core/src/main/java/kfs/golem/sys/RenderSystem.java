package kfs.golem.sys;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import kfs.golem.GolemMain;
import kfs.golem.comp.*;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.EntityFilter;
import kfs.golem.ecs.KfsSystem;
import kfs.golem.shaders.ShaderEffect;
import kfs.golem.utils.BubbleStyle;

import java.util.List;

public class RenderSystem implements KfsSystem {

    private final GolemMain golemMain;
    private final BitmapFont subtitleFont;
    private final BitmapFont dialogFont;
    private final GlyphLayout layout;

    public RenderSystem(GolemMain golemMain, BitmapFont subtitleFont, BitmapFont dialogFont) {
        this.golemMain = golemMain;
        this.subtitleFont = subtitleFont;
        this.dialogFont = dialogFont;
        layout = new GlyphLayout();
    }

    public void render(SpriteBatch batch, EntityFilter filter) {
        List<Entity> lst1 = golemMain.world.getEntitiesWith(PositionComponent.class, TextureComponent.class);
        for (Entity e : lst1) {
            if (filter.filter(e)) {
                renderTexturesFullScreen(batch, e);
            }
        }

        for (Entity e : lst1) {
            if (filter.filter(e)) {
                renderTextures(batch, e);
            }
        }

        for (Entity subtitle : golemMain.world.getEntitiesWith(SubtitleComponent.class)) {
            if (filter.filter(subtitle)) {
                renderSubtitles(batch, subtitle);
            }
        }

        for (Entity e : golemMain.world.getEntitiesWith(SubtitleMultilineComponent.class)) {
            if (filter.filter(e)) {
                renderMultilineSubtitles(batch, e);
            }
        }

        for (Entity e : golemMain.world.getEntitiesWith(DialogComponent.class)) {
            if (filter.filter(e)) {
                renderDialogs(batch, e);
            }
        }
        batch.end();
        batch.setShader(null);

        for (Entity e : lst1) {
            if (filter.filter(e)) {
                renderTexturesWithShader(batch, e);
            }
        }
        batch.setShader(null);

    }

    private void renderTexturesFullScreen(SpriteBatch batch, Entity e) {
        TextureComponent tex = golemMain.world.getComponent(e, TextureComponent.class);
        if (tex.windowSize) {
            PositionComponent pos = golemMain.world.getComponent(e, PositionComponent.class);
            batch.setColor(tex.tint.r, tex.tint.g, tex.tint.b, tex.alpha);
            Vector2 wSize = golemMain.getWorldSize();
            float imgW = tex.texture.getWidth();
            float imgH = tex.texture.getHeight();
            float scale = Math.min(wSize.x / imgW, wSize.y / imgH);
            batch.draw(tex.texture,
                pos.position.x, pos.position.y,
                imgW * scale,
                imgH * scale
            );
            batch.setColor(1f, 1f, 1f, 1f);
        }
    }

    private void renderTextures(SpriteBatch batch, Entity e) {
        TextureComponent tex = golemMain.world.getComponent(e, TextureComponent.class);
        if (tex.windowSize) return;
        ShaderLocalComponent shaderLocal = golemMain.world.getComponent(e, ShaderLocalComponent.class);
        if ( shaderLocal != null) return;
        PositionComponent pos = golemMain.world.getComponent(e, PositionComponent.class);
        SizeComponent sz = golemMain.world.getComponent(e, SizeComponent.class);
        batch.setColor(tex.tint.r, tex.tint.g, tex.tint.b, tex.alpha);
        if (sz != null) {
            batch.draw(tex.texture, pos.position.x, pos.position.y, sz.width(), sz.height());
        } else {
            batch.draw(tex.texture, pos.position.x, pos.position.y);
        }
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void renderTexturesWithShader(SpriteBatch batch, Entity e) {
        ShaderLocalComponent shaderLocal = golemMain.world.getComponent(e, ShaderLocalComponent.class);
        if (shaderLocal == null || !shaderLocal.enabled) return;
        TextureComponent tex = golemMain.world.getComponent(e, TextureComponent.class);
        PositionComponent pos = golemMain.world.getComponent(e, PositionComponent.class);
        SizeComponent sz = golemMain.world.getComponent(e, SizeComponent.class);
        ShaderEffect se = golemMain.shaderPipeline.getEffect(shaderLocal.type);
        if (se != null) {
            batch.begin();
            se.shader.bind();
            se.setUniforms(e, shaderLocal, 0.01f);
            batch.setShader(se.shader);
            batch.draw(tex.texture, pos.position.x, pos.position.y, sz.width(), sz.height());
            batch.end();
            batch.setShader(null);
        }
    }

    private void renderSubtitles(SpriteBatch batch, Entity subtitle) {
        SubtitleComponent sc = golemMain.world.getComponent(subtitle, SubtitleComponent.class);
        subtitleFont.setColor(sc.color);
        subtitleFont.draw(batch, sc.text, sc.position.x, sc.position.y);
    }

    private void renderMultilineSubtitles(SpriteBatch batch, Entity e) {
        SubtitleMultilineComponent s = golemMain.world.getComponent(e, SubtitleMultilineComponent.class);
        subtitleFont.setColor(s.color);

        float scroll = s.scrollOffset / s.lineHeight;
        int whole = (int)Math.floor(scroll);
        float frac = scroll - whole;


        for (int i = 0; i < s.maxLines; i++) {
            int lineIndex = (whole - s.maxLines) + i;
            if (lineIndex < 0 || lineIndex >= s.lines.size()) continue;

            float y = s.baseY + (s.maxLines-i) * s.lineHeight + frac * s.lineHeight;

            float alpha = 1.0f;

            if ((i == 0) && (frac > 1.0f - s.fadeDuration)){
                float t = (frac - (1.0f - s.fadeDuration)) / s.fadeDuration;
                alpha = 1.0f - t;
            }

            if ((i == s.maxLines-1) && (frac < s.fadeDuration)) {
                alpha = frac / s.fadeDuration;
            }

            Color old = subtitleFont.getColor();
            subtitleFont.setColor(s.color.r, s.color.g, s.color.b, alpha);
            subtitleFont.draw(batch, s.lines.get(lineIndex), s.baseX, y, 1100, Align.left, false);
            subtitleFont.setColor(old);
        }
    }

    private void renderDialogs(SpriteBatch batch, Entity e) {
        DialogComponent dlg = golemMain.world.getComponent(e, DialogComponent.class);
        PositionComponent pos = golemMain.world.getComponent(e, PositionComponent.class);
        layout.setText(dialogFont, dlg.text, Color.BLACK, dlg.style.maxWidth, Align.left, true);
        dialogFont.setColor(dlg.textColor);
        dialogFont.draw(batch, layout,
            pos.position.x + dlg.style.padding + ((dlg.style.tail == BubbleStyle.BubbleTail.LEFT)?dlg.style.tailSize:0),
            pos.position.y + dlg.size.y - ((dlg.style.tail == BubbleStyle.BubbleTail.DOWN)?0:dlg.style.tailSize));
    }

}
