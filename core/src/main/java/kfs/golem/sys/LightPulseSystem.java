package kfs.golem.sys;

import kfs.golem.GolemMain;
import kfs.golem.comp.LightPulseComponent;
import kfs.golem.comp.TextureComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsSystem;

public class LightPulseSystem implements KfsSystem {

    private final GolemMain golemMain;

    public LightPulseSystem(GolemMain golemMain) {
        this.golemMain = golemMain;
    }

    @Override
    public void update(float delta) {
        for (Entity e : golemMain.world.getEntitiesWith(LightPulseComponent.class)) {
            LightPulseComponent lpc = golemMain.world.getComponent(e, LightPulseComponent.class);
            TextureComponent tc = golemMain.world.getComponent(e, TextureComponent.class);
            lpc.time += delta;
            tc.alpha = 0.85f + (float)Math.sin(lpc.time * 2.2) * 0.15f;

        }
    }
}
