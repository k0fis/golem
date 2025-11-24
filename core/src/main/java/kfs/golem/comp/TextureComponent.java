package kfs.golem.comp;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;

public class TextureComponent implements KfsComp {
    public Texture texture;
    public float alpha = 1f;
    public Color tint = new Color(1, 1, 1, 1);
    public boolean windowSize = false;
    public ShaderEffectComponent shader = null;
    public ShaderEffectComponent shaderSwap = null;
    public Entity shaderEntity = null;

    public TextureComponent(Texture texture, boolean fullSize) { this.texture = texture; this.windowSize = fullSize; }

    public void swapShaders() {
        ShaderEffectComponent s = shader;
        shader = shaderSwap;
        shaderSwap = s;
    }
}
