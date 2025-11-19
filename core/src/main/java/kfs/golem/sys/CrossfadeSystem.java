package kfs.golem.sys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import kfs.golem.GolemMain;
import kfs.golem.comp.CrossfadeComponent;
import kfs.golem.comp.ShaderComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsSystem;
import kfs.golem.shaders.ShaderEffect;

public class CrossfadeSystem extends ShaderEffect implements KfsSystem {

    private final Pixmap dummyWhitePixel;
    private final Texture dummyWhiteTexture;

    public CrossfadeSystem(GolemMain golem) {
        super(golem, GolemMain.ShaderType.CROSS_FADE, "shaders/crossfade.vert", "shaders/crossfade.frag");

        dummyWhitePixel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        dummyWhitePixel.setColor(1, 1, 1, 1);
        dummyWhitePixel.fill();
        dummyWhiteTexture = new Texture(dummyWhitePixel);
    }

    @Override
    public void update(float delta) {
        for (Entity e : golemMain.world.getEntitiesWith(CrossfadeComponent.class)) {
            CrossfadeComponent cc = golemMain.world.getComponent(e, CrossfadeComponent.class);
            if (cc.time > cc.duration) {
                if (cc.onFinish != null) {
                    cc.onFinish.run();
                }
                golemMain.world.deleteEntity(e);
            } else {
                cc.time += delta;
                cc.fade = cc.time / cc.duration;
                if (cc.fade < 0) { cc.fade = 0;}
                if (cc.fade > 1) { cc.fade = 1;}
            }
        }
    }

    @Override
    protected void setUniforms(Entity e, ShaderComponent sc, float delta) {
    }

    public int render(Batch batch, FrameBuffer fba, FrameBuffer fbb) {
        int inx = 0;
        for (Entity e : golemMain.world.getEntitiesWith(CrossfadeComponent.class)) {
            CrossfadeComponent cc = golemMain.world.getComponent(e, CrossfadeComponent.class);

            batch.begin();
            batch.setShader(shader);
            shader.bind();
            shader.setUniformf("u_fade", cc.fade);

            fba.getColorBufferTexture().bind(0);
            shader.setUniformi("u_textureA", 0);

            fbb.getColorBufferTexture().bind(1);
            shader.setUniformi("u_textureB", 1);

            // LibGDX chce bindnout texturu, kterou batch.draw používá:
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

           Vector2 size = golemMain.getWorldSize();

            batch.draw(dummyWhiteTexture, 0, 0, size.x, size.y, 0, 0, 1, 1);

            batch.end();
            batch.setShader(null);
            inx++;
        }
        return inx;
    }

    @Override
    public void dispose() {
        super.dispose();
        dummyWhiteTexture.dispose();
        dummyWhitePixel.dispose();
    }
}
