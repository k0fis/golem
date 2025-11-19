package kfs.golem.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import kfs.golem.GolemMain;
import kfs.golem.comp.ShaderComponent;
import kfs.golem.comp.TextureComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.EntityFilter;

public abstract class ShaderEffect {

    protected final GolemMain golemMain;
    protected ShaderProgram shader;
    protected GolemMain.ShaderType type;

    protected ShaderEffect(GolemMain golemMain, GolemMain.ShaderType type, String vert, String frag) {
        this.golemMain = golemMain;
        this.type = type;
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(Gdx.files.internal(vert), Gdx.files.internal(frag));

        if (!shader.isCompiled()) {
            Gdx.app.error("ShaderEffect", "compile error for " + vert+", " + frag +": " + shader.getLog());
            throw new RuntimeException("compile error for " + vert+", " + frag +": " + shader.getLog());
        }
    }

    public void setShader(SpriteBatch batch, EntityFilter filter) {
        for (Entity e : golemMain.world.getEntitiesWith(ShaderComponent.class)) {
            if (filter.filter(e)) {
                ShaderComponent sc = golemMain.world.getComponent(e, ShaderComponent.class);
                if (sc.type == type) {
                    batch.setShader(shader);
                    shader.bind();
                }
            }
        }
    }


    public void setUniforms(float delta, EntityFilter filter) {
        for (Entity e : golemMain.world.getEntitiesWith(ShaderComponent.class)) {
            if (filter.filter(e)) {
                ShaderComponent sc = golemMain.world.getComponent(e, ShaderComponent.class);
                if (sc.type == type) {
                    sc.time += delta;
                    setUniforms(e, sc, delta);
                }
            }
        }
    }



    protected abstract void setUniforms(Entity e, ShaderComponent sc, float delta) ;

    protected void setTime(Entity e, ShaderComponent sc) {
        shader.setUniformf("u_time", sc.time);
    }

    protected void setResolution(Entity e, ShaderComponent sc) {
        TextureComponent tex = golemMain.world.getComponent(sc.texEntity==null?e:sc.texEntity, TextureComponent.class);
        Vector2 wSize = golemMain.getWorldSize();
        float imgW = tex.texture.getRegionWidth();
        float imgH = tex.texture.getRegionHeight();
        float scale = Math.min(wSize.x / imgW, wSize.y / imgH);
        shader.setUniformf("u_resolution", imgW * scale, imgH * scale);
    }
    public void dispose() {
        shader.dispose();
    }
}
