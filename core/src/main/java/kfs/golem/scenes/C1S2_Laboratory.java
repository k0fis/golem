package kfs.golem.scenes;

import kfs.golem.GolemMain;
import kfs.golem.comp.ShaderComponent;
import kfs.golem.ecs.Entity;

public class C1S2_Laboratory implements SceneLoader {

    @Override
    public void load(GolemMain engine) {
        Entity bgEntity = engine.createLayer("images/prague_bridge.png", 0.5f);
        engine.createSubtitle("Karluv Most", null);
        engine.world.addComponent(bgEntity, new ShaderComponent(GolemMain.ShaderType.FOG));
    }
}
