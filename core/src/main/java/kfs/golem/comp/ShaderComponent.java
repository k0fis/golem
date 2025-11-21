package kfs.golem.comp;

import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.shaders.ShaderType;

public class ShaderComponent implements KfsComp {

    public float time = 0.0f;
    public final ShaderType type;
    public final Entity texEntity;

    public ShaderComponent(ShaderType type, Entity texEntity) {
        this.type = type;
        this.texEntity = texEntity;
    }

    public ShaderComponent(String shaderName, Entity texEntity) {
        this(ShaderType.fromShaderName(shaderName, true), texEntity);
    }

}
