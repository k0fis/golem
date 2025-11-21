package kfs.golem.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import kfs.golem.GolemMain;
import kfs.golem.comp.ShaderComponent;
import kfs.golem.comp.ShaderLocalComponent;
import kfs.golem.comp.TextureComponent;
import kfs.golem.ecs.Entity;

public abstract class ShaderEffect {

    protected final GolemMain golemMain;
    public ShaderProgram shader;

    protected ShaderEffect(GolemMain golemMain, String vert, String frag) {
        this.golemMain = golemMain;
        shader = new ShaderProgram(Gdx.files.internal(vert), Gdx.files.internal(frag));

        if (!shader.isCompiled()) {
            Gdx.app.error("ShaderEffect", "compile error for " + vert+", " + frag +": " + shader.getLog());
            throw new RuntimeException("compile error for " + vert+", " + frag +": " + shader.getLog());
        }
    }

    public void setUniforms(Entity e, ShaderLocalComponent slc, float delta) {}

    protected void setUniforms(Entity e, ShaderComponent sc, float delta) {}

    protected void setTime(Entity e, ShaderComponent sc) {
        shader.setUniformf("u_time", sc.time);
    }

    protected void setResolution(Entity e, ShaderComponent sc) {
        TextureComponent tex = golemMain.world.getComponent(sc.texEntity==null?e:sc.texEntity, TextureComponent.class);
        if (tex != null) {
            Vector2 wSize = golemMain.getWorldSize();
            float imgW = tex.texture.getWidth();
            float imgH = tex.texture.getHeight();
            float scale = Math.min(wSize.x / imgW, wSize.y / imgH);
            shader.setUniformf("u_resolution", imgW * scale, imgH * scale);
        }
    }

    public void dispose() {
        shader.dispose();
    }
}
