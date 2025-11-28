package kfs.golem.sys;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import kfs.golem.GolemMain;
import kfs.golem.comp.*;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsSystem;
import kfs.golem.scenes.ShaderType;
import kfs.golem.utils.BubbleStyle;

import java.util.*;

public class RenderSystem implements KfsSystem {

    private final GolemMain golemMain;
    private final BitmapFont subtitleFont;
    private final BitmapFont dialogFont;
    private final GlyphLayout layout;
    public final Vector2 wSize;
    private FrameBuffer fbCurrent;
    private FrameBuffer fbSecond;
    private final SpriteBatch batch = new SpriteBatch();
    public float scale = 1f;
    public Texture lastTexture;

    public RenderSystem(GolemMain golemMain, BitmapFont subtitleFont, BitmapFont dialogFont,
                        float width, float height) {
        this.golemMain = golemMain;
        this.subtitleFont = subtitleFont;
        this.dialogFont = dialogFont;
        wSize = new Vector2(width, height);
        layout = new GlyphLayout();
    }

    @Override
    public void update(float delta) {
        lastTexture = null;
        if (golemMain.sceneCurrent != null) {
            Map<Integer, ArrayList<Entity>> zomap = new HashMap<>();
            for (Entity e : golemMain.world.getEntitiesWith(RenderComponent.class)) {
                RenderComponent rc = golemMain.world.getComponent(e, RenderComponent.class);
                if (rc.sceneIdClass == golemMain.sceneCurrent.getSLClass()) {
                    zomap.computeIfAbsent(rc.zOrder, q->new ArrayList<>()).add(e);
                }
            }
            for (int z : zomap.keySet().stream().sorted().toList()){
                fbCurrent.begin();
                batch.begin();
                if (lastTexture != null) {
                    batch.draw(lastTexture, 0f, 0f, fbCurrent.getWidth(), fbCurrent.getHeight(),
                        0, 0, 1, 1);
                }

                for (Entity e : zomap.get(z)) {
                    golemMain.world.getComponent(e, RenderComponent.class).onRender.render(this);
                }

                batch.end();
                fbCurrent.end();
                lastTexture = fbCurrent.getColorBufferTexture();
                FrameBuffer swap = fbCurrent;
                fbCurrent = fbSecond;
                fbSecond = swap;
            }
            batch.begin();
            // cross fade
            Optional<Entity> oe = golemMain.world.getEntityWith(CrossfadeComponent.class);
            if (oe.isPresent()) {
                TimerComponent timerComp = golemMain.world.getComponent(oe.get(), TimerComponent.class);
                batch.setColor(1, 1, 1, timerComp.time / timerComp.limit);
            }
            batch.draw(lastTexture, 0f, 0f, fbCurrent.getWidth(), fbCurrent.getHeight(),
                    0, 0, 1, 1);
            batch.end();
        }
    }

    public void resize(int width, int height) {
        wSize.set(width, height);
        if (fbCurrent != null) fbCurrent.dispose();
        if (fbSecond != null) fbSecond.dispose();
        fbCurrent = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        fbSecond = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
    }

    @Override
    public void done() {
        if (fbCurrent != null) fbCurrent.dispose();
        if (fbSecond != null) fbSecond.dispose();
        batch.dispose();
    }

    public void renderTexturesFullScreen(Entity e) {
        TextureComponent tex = golemMain.world.getComponent(e, TextureComponent.class);
        PositionComponent pos = golemMain.world.getComponent(e, PositionComponent.class);
        float imgW = tex.texture.getWidth();
        float imgH = tex.texture.getHeight();
        scale = Math.min(wSize.x / imgW, wSize.y / imgH);
        batch.draw(tex.texture, pos.position.x, pos.position.y, imgW * scale ,imgH * scale);
    }

    public void renderFsEffect(ShaderType type, Map<String, Float> params) {
        for (Entity e : golemMain.world.getEntitiesWith(PostEffectComponent.class, type.shaderComponent)) {
            Optional.of(golemMain.world.getComponent(e, PostEffectComponent.class))
                .ifPresent(pec->{
                    pec.apply(batch, e, lastTexture, wSize, params);
                });
        }
    }

    public void renderTextures(Entity e) {
        TextureComponent tex = golemMain.world.getComponent(e, TextureComponent.class);
        PositionComponent pos2 = golemMain.world.getComponent(e, PositionComponent.class);
        SizeComponent sz2 = golemMain.world.getComponent(e, SizeComponent.class);
        Vector2 pos = pos2.position.cpy().scl(scale);
        Vector2 sz = ((sz2!=null)?sz2.size.cpy():new Vector2(tex.texture.getWidth(), tex.texture.getHeight())).scl(scale);
        if (tex.shader != null) {
            if (tex.shader.shaderTexture != null) {
                batch.draw(tex.texture, pos.x, pos.y, sz.x, sz.y);
                if (tex.shader.enabled) {
                    tex.shader.shader.apply(batch, tex.shader.shaderTexture, tex.shader.params,
                        tex.shader.shaderEntity, pos, sz);
                } else {
                    batch.draw(tex.shader.shaderTexture, pos.x, pos.y, sz.x, sz.y);
                }
            } else {
                if (tex.shader.enabled) {
                    tex.shader.shader.apply(batch, tex.texture, tex.shader.params,
                        tex.shader.shaderEntity, pos, sz);
                } else {
                    batch.draw(tex.texture, pos.x, pos.y, sz.x, sz.y);
                }
            }
        }
        if (!tex.hasShaders()) {
            batch.setColor(tex.tint.r, tex.tint.g, tex.tint.b, tex.alpha);
            batch.draw(tex.texture, pos.x, pos.y, sz.x, sz.y);
        }
    }


    public void renderSubtitles(Entity subtitle) {
        SubtitleComponent sc = golemMain.world.getComponent(subtitle, SubtitleComponent.class);
        subtitleFont.setColor(sc.color);
        subtitleFont.draw(batch, sc.text, sc.position.x, sc.position.y);
    }

    public void renderMultilineSubtitles(Entity e) {
        SubtitleMultilineComponent s = golemMain.world.getComponent(e, SubtitleMultilineComponent.class);
        subtitleFont.setColor(s.color);

        float scroll = s.scrollOffset / s.lineHeight;
        int whole = (int) Math.floor(scroll);
        float frac = scroll - whole;


        for (int i = 0; i < s.maxLines; i++) {
            int lineIndex = (whole - s.maxLines) + i;
            if (lineIndex < 0 || lineIndex >= s.lines.size()) continue;

            float y = s.baseY + (s.maxLines - i) * s.lineHeight + frac * s.lineHeight;

            float alpha = 1.0f;

            if ((i == 0) && (frac > 1.0f - s.fadeDuration)) {
                float t = (frac - (1.0f - s.fadeDuration)) / s.fadeDuration;
                alpha = 1.0f - t;
            }

            if ((i == s.maxLines - 1) && (frac < s.fadeDuration)) {
                alpha = frac / s.fadeDuration;
            }

            Color old = subtitleFont.getColor();
            subtitleFont.setColor(s.color.r, s.color.g, s.color.b, alpha);
            subtitleFont.draw(batch, s.lines.get(lineIndex), s.baseX, y, 1100, Align.left, false);
            subtitleFont.setColor(old);
        }
    }

    public void renderDialog(Entity e) {
        DialogComponent dlg = golemMain.world.getComponent(e, DialogComponent.class);
        PositionComponent pos2 = golemMain.world.getComponent(e, PositionComponent.class);

        Vector2 ppos = pos2.position.cpy()
            .add(dlg.style.padding + ((dlg.style.tail == BubbleStyle.BubbleTail.LEFT) ? dlg.style.tailSize : 0),
                dlg.size.y - ((dlg.style.tail == BubbleStyle.BubbleTail.DOWN) ? 0 : dlg.style.tailSize)
            ).scl(scale);

        layout.setText(dialogFont, dlg.text, Color.BLACK, dlg.style.maxWidth, Align.left, true);
        dialogFont.setColor(dlg.textColor);
        dialogFont.draw(batch, layout, ppos.x , ppos.y);

    }
}
