package kfs.golem.comp;

import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.shaders.ShaderType;

public class ShaderLocalComponent implements KfsComp {

    public float time = 0.0f;
    public boolean enabled = true;
    public final ShaderType type;

    public ShaderLocalComponent(String shaderName) {
        this.type = ShaderType.fromShaderName(shaderName, true);
    }

}
