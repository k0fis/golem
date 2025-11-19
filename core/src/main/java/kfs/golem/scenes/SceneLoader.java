package kfs.golem.scenes;

import kfs.golem.GolemMain;

import java.util.ServiceLoader;

public interface SceneLoader {

    void load(GolemMain engine);

    void unload(GolemMain engine);

    Class<? extends SceneLoader> getSLClass();
}
