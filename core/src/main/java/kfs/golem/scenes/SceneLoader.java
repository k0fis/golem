package kfs.golem.scenes;

import kfs.golem.GolemMain;

public interface SceneLoader {

    void load(GolemMain engine);

    default void unload(GolemMain engine) {
        engine.world.reset();
    }
}
