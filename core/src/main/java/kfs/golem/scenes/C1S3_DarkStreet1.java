package kfs.golem.scenes;

import kfs.golem.GolemMain;
import kfs.golem.comp.InteractiveComponent;
import kfs.golem.comp.ShaderEffectComponent;
import kfs.golem.comp.TextureComponent;
import kfs.golem.ecs.Entity;

public class C1S3_DarkStreet1 extends  SceneLoader {

    public C1S3_DarkStreet1(GolemMain golem) {
        super(golem, "scenes/C1S3_DarkStreet1.json");
    }


    @Override
    public void load() {
        super.load();

        Entity lamp = java.util.Objects.requireNonNull(textures.get("c1s1-lamp-1"), "lamp is null");
        TextureComponent tc = golemMain.world.getComponent(lamp, TextureComponent.class);
        golemMain.world.addComponent(lamp, new InteractiveComponent(tc::swapShaders,
            tc.texture.getWidth(), tc.texture.getHeight()));
    }

    @Override
    public Class<? extends SceneLoader> getSLClass() {
        return getClass();
    }
}
