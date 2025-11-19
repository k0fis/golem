package kfs.golem.comp;

import kfs.golem.GolemMain;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;

public class ShaderComponent implements KfsComp {

    public float time = 0.0f;
    public final GolemMain.ShaderType type;
    public final Entity texEntity;

    public ShaderComponent(GolemMain.ShaderType type) {
        this(type, null);
    }
    public ShaderComponent(GolemMain.ShaderType type, Entity texEntity) {
        this.type = type;
        this.texEntity = texEntity;
    }
}
