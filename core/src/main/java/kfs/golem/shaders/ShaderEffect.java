package kfs.golem.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import kfs.golem.GolemMain;
import kfs.golem.comp.ShaderComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsWorld;

public abstract class ShaderEffect {

    protected final KfsWorld world;
    protected ShaderProgram shader;
    protected GolemMain.ShaderType type;

    protected ShaderEffect(KfsWorld world, GolemMain.ShaderType type, String vert, String frag) {
        this.world = world;
        this.type = type;
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(Gdx.files.internal(vert), Gdx.files.internal(frag));

        if (!shader.isCompiled()) {
            Gdx.app.error("ShaderEffect", "compile error for " + vert+", " + frag +": " + shader.getLog());
            throw new RuntimeException("compile error for " + vert+", " + frag +": " + shader.getLog());
        }
    }

    public void setShader(SpriteBatch batch) {
        for (Entity e : world.getEntitiesWith(ShaderComponent.class)) {
            ShaderComponent sc = world.getComponent(e, ShaderComponent.class);
            if (sc.type == type) {
                batch.setShader(shader);
            }
        }
    }


    public void setUniforms(float delta) {
        for (Entity e : world.getEntitiesWith(ShaderComponent.class)) {
            ShaderComponent sc = world.getComponent(e, ShaderComponent.class);
            if (sc.type == type) {
                sc.time += delta;
                setUniforms(e, sc, delta);
            }
        }
    }

    protected abstract void setUniforms(Entity e, ShaderComponent sc, float delta);

    public void dispose() {
        shader.dispose();
    }
}
