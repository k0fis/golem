package kfs.golem.scenes;

import kfs.golem.GolemMain;
import kfs.golem.comp.InteractiveComponent;
import kfs.golem.comp.TextureComponent;
import kfs.golem.comp.TimeComponent;
import kfs.golem.ecs.Entity;

import java.util.Optional;

public class C1S5_AlchemystWorkShop extends SceneLoader {
    public C1S5_AlchemystWorkShop(GolemMain golemMain) {
        super(golemMain, "scenes/C1S5_AlchemistWorkShop.json");
    }

    @Override
    public void load() {
        super.load();
        //
        Optional.ofNullable(textures.get("symbol-2")).ifPresent(e->{
            TextureComponent tex = golemMain.world.getComponent(e, TextureComponent.class);
            TimeComponent tc = golemMain.world.getComponent(tex.shader.shaderEntity, TimeComponent.class);
            golemMain.world.addComponent(e, new InteractiveComponent(tc::rst,200,200));
        });

    }

    @Override
    public Class<? extends SceneLoader> getSLClass() {
        return getClass();
    }
}
