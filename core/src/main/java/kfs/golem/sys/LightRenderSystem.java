package kfs.golem.sys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import kfs.golem.GolemMain;
import kfs.golem.comp.LightComponent;
import kfs.golem.comp.TransformComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsSystem;

public class LightRenderSystem implements KfsSystem {

    private final GolemMain golemMain;

    private final ShaderProgram lightShader;
    private final Texture white;
    private float time = 0f;

    public LightRenderSystem(GolemMain golemMain) {
        this.golemMain = golemMain;
        this.lightShader = new ShaderProgram(
            Gdx.files.internal("shaders/lamp.vert"),
            Gdx.files.internal("shaders/lamp.frag")
        );

        if (!lightShader.isCompiled()) {
            Gdx.app.error("ShaderSystem", "LIGHT SHADER ERROR: " + lightShader.getLog());
        }
        white = new Texture("textures/white.png");
    }

    @Override
    public void update(float delta) {
        time += delta;
    }

    @Override
    public void render(SpriteBatch batch) {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        for (Entity e : golemMain.world.getEntitiesWith(LightComponent.class, TransformComponent.class)) {
            LightComponent light = golemMain.world.getComponent(e, LightComponent.class);
            TransformComponent tr = golemMain.world.getComponent(e, TransformComponent.class);

            float u = tr.position.x / w;
            float v = tr.position.y / h;

            lightShader.setUniformf("u_lightPos", u, v);
            lightShader.setUniformf("u_radius", light.radius);
            lightShader.setUniformf("u_intensity", light.intensity);
            lightShader.setUniformf("u_color", new Vector3(light.color.r, light.color.g, light.color.b));
            lightShader.setUniformf("u_time", time);

            batch.draw(white, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        }
    }
}
