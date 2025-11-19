package kfs.golem.comp;

import kfs.golem.ecs.KfsComp;
import kfs.golem.scenes.SceneLoader;

public class SceneIdComponent implements KfsComp {

    public Class<? extends SceneLoader> sceneIdClass;

    public SceneIdComponent(Class<? extends SceneLoader> sceneIdClass) {
        this.sceneIdClass = sceneIdClass;
    }
}
