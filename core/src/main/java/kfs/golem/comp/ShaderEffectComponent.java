package kfs.golem.comp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.utils.ShaderEffectCompileException;

public class ShaderEffectComponent implements KfsComp {

    public final ShaderProgram shader;
    public final Texture texture;
    public final PecParams params;


    public ShaderEffectComponent(String vert, String frag, Texture texture, PecParams params) {
        this.params = params;
        this.texture = texture;
        shader = new ShaderProgram(Gdx.files.internal(vert), Gdx.files.internal(frag));
        if (!shader.isCompiled()) {
            throw new ShaderEffectCompileException(vert, frag, shader.getLog());
        }
    }

    public void dispose() {
        shader.dispose();
        texture.dispose();
    }

    public void apply(Batch batch, Entity entity, Vector2 position, Vector2 size) {
        batch.setShader(shader);
        if (params != null) {
            params.setUniforms(entity, this);
        }
        batch.draw(texture, position.x, position.y, size.x, size.y);
        batch.setShader(null);
    }

    public interface PecParams {
        void setUniforms(Entity entity, ShaderEffectComponent pec);
    }


}
