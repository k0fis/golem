package kfs.golem.sys;

import kfs.golem.GolemMain;
import kfs.golem.comp.AnimationComponent;
import kfs.golem.comp.TextureComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsSystem;

public class AnimationSystem implements KfsSystem {

    private final GolemMain golemMain;

    public AnimationSystem(GolemMain golemMain) {
        this.golemMain = golemMain;
    }

    @Override
    public void update(float delta) {
        for (Entity e : golemMain.world.getEntitiesWith(AnimationComponent.class, TextureComponent.class)) {
            AnimationComponent ac = golemMain.world.getComponent(e, AnimationComponent.class);
            if (ac.enabled) {
                TextureComponent tc = golemMain.world.getComponent(e, TextureComponent.class);
                ac.stateTime += delta;
                //tc.texture = ac.animation.getKeyFrame(ac.stateTime, true);
            }
        }
    }
}
