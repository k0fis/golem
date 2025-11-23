package kfs.golem.sys;

import kfs.golem.GolemMain;
import kfs.golem.comp.BrightenControllComponent;
import kfs.golem.comp.ShaderBrightenComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsSystem;

public class BrightenSystem implements KfsSystem {

    private final GolemMain golem;

    public BrightenSystem(GolemMain golem) {
        this.golem = golem;
    }

    @Override
    public void update(float delta) {
        updateBrightenControllComponent();
        updateBrightenComponent(delta);
    }

    private void updateBrightenControllComponent() {
        for (Entity c : golem.world.getEntitiesWith(BrightenControllComponent.class)) {
            BrightenControllComponent bcc = golem.world.getComponent(c, BrightenControllComponent.class);

            for (Entity e : golem.world.getEntitiesWith(ShaderBrightenComponent.class)) {
                ShaderBrightenComponent bc = golem.world.getComponent(e, ShaderBrightenComponent.class);

                if (bcc.toState == ShaderBrightenComponent.State.FadingIn) {
                    bc.fadeIn(bcc.speed, bcc.targetBrightness);
                } else if (bcc.toState == ShaderBrightenComponent.State.FadingOut) {
                    bc.fadeOut(bcc.speed, bcc.targetBrightness);
                }
            }

            golem.world.deleteEntity(c);
        }
    }

    private void updateBrightenComponent(float delta) {
        for (Entity e : golem.world.getEntitiesWith(ShaderBrightenComponent.class)) {
            ShaderBrightenComponent bc = golem.world.getComponent(e, ShaderBrightenComponent.class);

            switch (bc.state) {
                case FadingIn:
                    bc.brightness += delta / bc.speed;
                    if (bc.brightness >= bc.target) {
                        bc.brightness = bc.target;
                        bc.state = ShaderBrightenComponent.State.Idle;
                    }
                    break;
                case FadingOut:
                    bc.brightness -= delta / bc.speed;
                    if (bc.brightness <= bc.target) {
                        bc.brightness = bc.target;
                        bc.state = ShaderBrightenComponent.State.Idle;
                    }
                    break;
            }
        }
    }
}
