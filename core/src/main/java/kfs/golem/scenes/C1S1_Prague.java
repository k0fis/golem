package kfs.golem.scenes;

import kfs.golem.GolemMain;
import kfs.golem.comp.ShaderComponent;
import kfs.golem.ecs.Entity;

public class C1S1_Prague implements SceneLoader {

    @Override
    public void load(GolemMain engine) {
        Entity bgEntity = engine.createLayer("images/prague_mid.png", 0.5f);
        engine.createSubtitle("Město se probouzí. Dýchá těžce, jako starý člověk, který nespal klidně.",
            ()->engine.loadScene(3f, new C1S2_Laboratory()));
        engine.world.addComponent(bgEntity, new ShaderComponent(GolemMain.ShaderType.PAPER));
    }
}
