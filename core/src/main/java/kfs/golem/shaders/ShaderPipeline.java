package kfs.golem.shaders;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class ShaderPipeline {

    private final Array<ShaderEffect> effects = new Array<>();

    public ShaderPipeline() {
    }

    public void add(ShaderEffect effect) {
        effects.add(effect);
    }

    public void setShader(SpriteBatch batch) {
        for (ShaderEffect effect : effects) {
            effect.setShader(batch);
        }
    }

    public void setUniforms(float delta) {
        for (ShaderEffect effect : effects) {
            effect.setUniforms(delta);
        }
    }


    public void dispose() {
        for (ShaderEffect e : effects) e.dispose();
    }
}
