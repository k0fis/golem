package kfs.golem.shaders;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import kfs.golem.ecs.EntityFilter;

public class ShaderPipeline {

    private final Array<ShaderEffect> effects = new Array<>();

    public ShaderPipeline() {
    }

    public void add(ShaderEffect effect) {
        effects.add(effect);
    }

    public void setShader(SpriteBatch batch, EntityFilter filter) {
        for (ShaderEffect effect : effects) {
            effect.setShader(batch, filter);
        }
    }

    public void setUniforms(float delta, EntityFilter filter) {
        for (ShaderEffect effect : effects) {
            effect.setUniforms(delta, filter);
        }
    }


    public void dispose() {
        for (ShaderEffect e : effects) e.dispose();
    }
}
