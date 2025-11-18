package kfs.golem.sys;

import com.badlogic.gdx.Gdx;
import kfs.golem.GolemMain;
import kfs.golem.comp.ParallaxComponent;
import kfs.golem.comp.PositionComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsSystem;

public class ParallaxSystem implements KfsSystem {

    private final GolemMain golemMain;
    float time = 0;

    public ParallaxSystem(GolemMain golemMain) {
        this.golemMain = golemMain;
    }

    @Override
    public void update(float delta) {
        for (Entity e : golemMain.world.getEntitiesWith( PositionComponent.class, ParallaxComponent.class)) {
            PositionComponent pos = golemMain.world.getComponent(e, PositionComponent.class);
            ParallaxComponent p = golemMain.world.getComponent(e, ParallaxComponent.class);
            p.time += delta;
            float offset = (float)Math.sin(p.time * p.speed) * p.amplitude;
            pos.position.x = offset;
        }
    }
}
