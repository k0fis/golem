package kfs.golem.sys;

import kfs.golem.GolemMain;
import kfs.golem.comp.ShaderLocalComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsSystem;

public class ShraderLocalSystem implements KfsSystem {

    private final GolemMain golemMain;

    public ShraderLocalSystem(GolemMain golemMain) {
        this.golemMain = golemMain;
    }

    @Override
    public void update(float delta) {
        for (Entity e : golemMain.world.getEntitiesWith(ShaderLocalComponent.class)) {
            ShaderLocalComponent ac = golemMain.world.getComponent(e, ShaderLocalComponent.class);
            if (ac.enabled) {
                ac.time += delta;
            }
        }
    }
}
