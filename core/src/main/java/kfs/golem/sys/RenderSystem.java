package kfs.golem.sys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import kfs.golem.GolemMain;
import kfs.golem.comp.PositionComponent;
import kfs.golem.comp.ShaderComponent;
import kfs.golem.comp.TextureComponent;
import kfs.golem.comp.TransformComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsSystem;

public class RenderSystem implements KfsSystem {

    private final GolemMain golemMain;

    public RenderSystem(GolemMain golemMain) {
        this.golemMain = golemMain;
    }

    @Override
    public void render(SpriteBatch batch) {
        for (Entity e : golemMain.world.getEntitiesWith( PositionComponent.class, TextureComponent.class)) {
            PositionComponent pos = golemMain.world.getComponent(e, PositionComponent.class);
            TextureComponent tex = golemMain.world.getComponent(e, TextureComponent.class);

            batch.setColor(tex.tint.r, tex.tint.g, tex.tint.b, tex.alpha);
            TransformComponent  tr = golemMain.world.getComponent(e, TransformComponent.class);
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
}
