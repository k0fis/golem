package kfs.golem.scenes;

import kfs.golem.GolemMain;
import kfs.golem.comp.InteractiveComponent;
import kfs.golem.comp.TextureComponent;
import kfs.golem.ecs.Entity;

public class C1S4_DarkStreet extends  SceneLoader {

    public C1S4_DarkStreet(GolemMain golem) {
        super(golem, "scenes/C1S4_DarkStreet.json");
    }


    @Override
    public void load() {
        super.load();

        Entity lamp = java.util.Objects.requireNonNull(textures.get("c1s4-lamp-1"), "lamp is null");
        Entity doorOutline = java.util.Objects.requireNonNull(textures.get("c1s4-doors"), "doors is null");


        TextureComponent tc = golemMain.world.getComponent(lamp, TextureComponent.class);
        golemMain.world.addComponent(lamp, new InteractiveComponent(tc::swapShaders,
            tc.texture.getWidth(), tc.texture.getHeight()));
    }

    @Override
    public Class<? extends SceneLoader> getSLClass() {
        return getClass();
    }
}
