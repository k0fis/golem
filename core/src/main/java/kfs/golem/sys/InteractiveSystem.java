package kfs.golem.sys;

import kfs.golem.GolemMain;
import kfs.golem.comp.ClickComponent;
import kfs.golem.comp.InteractiveComponent;
import kfs.golem.comp.PositionComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsSystem;

public class InteractiveSystem implements KfsSystem {

    private final GolemMain golemMain;

    public InteractiveSystem(GolemMain golemMain) {
        this.golemMain = golemMain;
    }

    @Override
    public void update(float delta) {
        for (Entity click : golemMain.world.getEntitiesWith(ClickComponent.class)) {
            ClickComponent cc = golemMain.world.getComponent(click, ClickComponent.class);

            // camera: Vector clickPoint = getEngine().getSystem(RenderSystem.class).getCamera().unproject(cc.click);

            for (Entity interactiveEntity : golemMain.world.getEntitiesWith(InteractiveComponent.class, PositionComponent.class)) {
                PositionComponent pc = golemMain.world.getComponent(interactiveEntity, PositionComponent.class);
                InteractiveComponent ic = golemMain.world.getComponent(interactiveEntity, InteractiveComponent.class);

                if (!ic.isEnabled) continue;

                float d = cc.click.dst(pc.position);
                if (d < ic.radius) {
                    ic.onInteract.run();
                    break;
                }
            }
            golemMain.world.deleteEntity(click);
        }
    }
}
