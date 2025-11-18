package kfs.golem.sys;

import kfs.golem.GolemMain;
import kfs.golem.comp.TimerComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsSystem;

public class TimerSystem implements KfsSystem {

    private final GolemMain golemMain;

    public TimerSystem(GolemMain golemMain) {
        this.golemMain = golemMain;
    }

    @Override
    public void update(float delta) {
        for (Entity e : golemMain.world.getEntitiesWith(TimerComponent.class)) {
            TimerComponent tc = golemMain.world.getComponent(e, TimerComponent.class);

            tc.time += delta;

            if (tc.time > tc.limit) {
                tc.time = 0;
                tc.count--;
                tc.action.run();
                if (tc.count <= 0) {
                    golemMain.world.deleteEntity(e);
                }
            }
        }
    }
}
