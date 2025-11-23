package kfs.golem.comp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.utils.ShaderEffectCompileException;

public class PostEffectComponent implements KfsComp {

    public final ShaderProgram shader;
    public final PecParams params;
    public FrameBuffer buffer = null;


    public PostEffectComponent(String vert, String frag, PecParams params) {
        this.params = params;

        shader = new ShaderProgram(Gdx.files.internal(vert), Gdx.files.internal(frag));
        if (!shader.isCompiled()) {
            throw new ShaderEffectCompileException(vert, frag, shader.getLog());
        }
    }

    public void dispose() {
        if (buffer != null) {
            buffer.dispose();
        }
        shader.dispose();
    }

    public Texture apply(Batch batch, Texture src, Entity entity, float delta) {
        buffer.begin();
        batch.setShader(shader);
        if (params != null) {
            params.setUniforms(entity, this, delta);
        }

        batch.begin();
        batch.draw(src, 0, 0, buffer.getWidth(), buffer.getHeight(), 0f, 0f, 1f, 1f);
        batch.end();

        batch.setShader(null);
        buffer.end();
        return buffer.getColorBufferTexture();
    }

    public interface PecParams {
        void setUniforms(Entity entity, PostEffectComponent pec, float delta);
    }


}
