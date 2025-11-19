package kfs.golem.sys;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import kfs.golem.GolemMain;
import kfs.golem.comp.*;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.EntityFilter;
import kfs.golem.ecs.KfsSystem;

public class RenderSystem implements KfsSystem {

    private final GolemMain golemMain;
    private final BitmapFont font;

    public RenderSystem(GolemMain golemMain, BitmapFont font) {
        this.golemMain = golemMain;
        this.font = font;
    }

    public void render(SpriteBatch batch, EntityFilter filter) {
        for (Entity e : golemMain.world.getEntitiesWith(PositionComponent.class, TextureComponent.class)) {
            if (filter.filter(e)) {
                PositionComponent pos = golemMain.world.getComponent(e, PositionComponent.class);
                TextureComponent tex = golemMain.world.getComponent(e, TextureComponent.class);

                batch.setColor(tex.tint.r, tex.tint.g, tex.tint.b, tex.alpha);
                TransformComponent tr = golemMain.world.getComponent(e, TransformComponent.class);
                if (tr != null) {
                    batch.draw(tex.texture, tr.position.x, tr.position.y, tr.originX, tr.originY,
                        tex.texture.getTexture().getWidth(), tex.texture.getTexture().getHeight(),
                        tr.scaleX, tr.scaleY, tr.rotation
                    );
                } else {
                    if (tex.windowSize) {
                        Vector2 wSize = golemMain.getWorldSize();
                        float imgW = tex.texture.getRegionWidth();
                        float imgH = tex.texture.getRegionHeight();
                        float scale = Math.min(wSize.x / imgW, wSize.y / imgH);
                        batch.draw(tex.texture,
                            pos.position.x, pos.position.y,
                            imgW * scale,
                            imgH * scale
                        );
                    } else {
                        batch.draw(tex.texture, pos.position.x, pos.position.y);
                    }
                }
                batch.setColor(1f, 1f, 1f, 1f);
            }
        }
        for (Entity subtitle : golemMain.world.getEntitiesWith(SubtitleComponent.class)) {
            if (filter.filter(subtitle)) {
                SubtitleComponent sc = golemMain.world.getComponent(subtitle, SubtitleComponent.class);
                font.setColor(sc.color);
                font.draw(batch, sc.text, sc.position.x, sc.position.y);
            }
        }
        for (Entity e : golemMain.world.getEntitiesWith(DialogueComponent.class)) {
            if (filter.filter(e)) {
                DialogueComponent dlg = golemMain.world.getComponent(e, DialogueComponent.class);
                if (!dlg.active) continue;
                PositionComponent pos = golemMain.world.getComponent(e, PositionComponent.class);

                font.setColor(1f, 1f, 1f, Math.min(1f, dlg.timeVisible));
                font.draw(batch, dlg.lines[dlg.index], pos.position.x, pos.position.y + 140);
            }
        }
    }
}
