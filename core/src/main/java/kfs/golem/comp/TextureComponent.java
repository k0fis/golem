package kfs.golem.comp;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.scenes.ShaderType;

import java.util.Map;

public class TextureComponent implements KfsComp {

    public static class Shader {
        public final Entity shaderEntity;
        public final ShaderEffectComponent shader;
        public final Map<String, Float> params;
        public final Texture shaderTexture;
        public final ShaderType shaderType;
        public boolean enabled = true;

        Shader(ShaderEffectComponent shader, Entity shaderEntity, Map<String, Float> params, Texture shaderTexture, ShaderType shaderType) {
            this.shader = shader;
            this.shaderEntity = shaderEntity;
            this.params = params;
            this.shaderTexture = shaderTexture;
            this.shaderType = shaderType;
        }
    }

    public Texture texture;
    public float alpha = 1f;
    public Color tint = new Color(1, 1, 1, 1);
    public Shader shader = null;

    public TextureComponent(Texture texture) {
        this.texture = texture;
    }

    public void setShader(ShaderEffectComponent shader, Entity shaderEntity, Map<String, Float> params, Texture shaderTexture, ShaderType shaderType) {
        this.shader = new Shader(shader, shaderEntity, params, shaderTexture, shaderType);
    }

    public boolean hasShaders() {
        return shader != null;
    }

    public void turn() {
        if (shader != null) {
            shader.enabled = !shader.enabled;
        }
    }

}
