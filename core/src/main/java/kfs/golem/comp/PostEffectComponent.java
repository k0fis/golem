package kfs.golem.comp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.utils.ShaderEffectCompileException;

import java.util.Map;

public class PostEffectComponent implements KfsComp {

    public final ShaderProgram shader;
    public final PecParams params;

    public PostEffectComponent(String vert, String frag, PecParams params) {
        this.params = params;

        shader = new ShaderProgram(Gdx.files.internal(vert), Gdx.files.internal(frag));
        if (!shader.isCompiled()) {
            throw new ShaderEffectCompileException(vert, frag, shader.getLog());
        }
    }

    public void dispose() {
        shader.dispose();
    }

    public void apply(Batch batch, Entity entity, Texture tex, Vector2 size, Map<String, Float> param) {
        batch.setColor(1,1,1,1);
        batch.enableBlending();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        batch.setShader(shader);
        params.setUniforms(entity, param, this, size);
        batch.draw(tex, 0f, 0f, size.x, size.y, 0,0,1,1);

        batch.setShader(null);
    }

    public interface PecParams {
        void setUniforms(Entity entity, Map<String, Float> params, PostEffectComponent pec, Vector2 wSize);
    }


}
