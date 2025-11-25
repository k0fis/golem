package kfs.golem.scenes;

import kfs.golem.GolemMain;
import kfs.golem.ecs.Entity;

public class C1S4_DarkStreet extends  SceneLoader {

    public C1S4_DarkStreet(GolemMain golem) {
        super(golem, "scenes/C1S4_DarkStreet.json");
    }


    @Override
    public void load() {
        super.load();

        Entity doorOutline = java.util.Objects.requireNonNull(textures.get("c1s4-doors"), "doors is null");
        createNextLoaderAction(doorOutline, new C1S5_AlchemystWorkShop(golemMain));

        setDefaultActionForLamp("c1s4-lamp-1");
        setDefaultActionForLamp("c1s4-lamp-2");
    }

    @Override
    public Class<? extends SceneLoader> getSLClass() {
        return getClass();
    }
}
