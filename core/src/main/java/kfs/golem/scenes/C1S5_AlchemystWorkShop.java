package kfs.golem.scenes;

import kfs.golem.GolemMain;

public class C1S5_AlchemystWorkShop extends SceneLoader {
    public C1S5_AlchemystWorkShop(GolemMain golemMain) {
        super(golemMain, "scenes/C1S5_AlchemistWorkShop.json");
    }

    @Override
    public void load() {
        super.load();
        //
    }

    @Override
    public Class<? extends SceneLoader> getSLClass() {
        return getClass();
    }
}
