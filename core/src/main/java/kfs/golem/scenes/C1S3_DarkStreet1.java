package kfs.golem.scenes;

import kfs.golem.GolemMain;

public class C1S3_DarkStreet1 extends  SceneLoader {

    public C1S3_DarkStreet1(GolemMain golem) {
        super(golem, "scenes/C1S3_DarkStreet1.json");
    }

    @Override
    public Class<? extends SceneLoader> getSLClass() {
        return getClass();
    }
}
