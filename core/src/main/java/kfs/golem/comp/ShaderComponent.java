package kfs.golem.comp;

import kfs.golem.GolemMain;
import kfs.golem.ecs.KfsComp;

public class ShaderComponent implements KfsComp {

    public float time = 0.0f;
    public final GolemMain.ShaderType type;

    public ShaderComponent(GolemMain.ShaderType type) {
        this.type = type;
    }
}
