package kfs.golem.comp;

import kfs.golem.ecs.KfsComp;

public class BrightenControllComponent implements KfsComp {

    public final ShaderBrightenComponent.State toState;
    public final float targetBrightness;
    public final float speed;

    public BrightenControllComponent(ShaderBrightenComponent.State state, float targetBrightness, float speed) {
        this.toState = state;
        this.targetBrightness = targetBrightness;
        this.speed = speed;
    }
}
