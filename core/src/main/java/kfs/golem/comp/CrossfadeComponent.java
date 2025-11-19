package kfs.golem.comp;

import kfs.golem.ecs.KfsComp;

public class CrossfadeComponent implements KfsComp {

    public final Runnable onFinish;
    public final float duration;
    public float time;
    public float fade;

    public CrossfadeComponent(float duration, Runnable onFinish) {
        this.onFinish = onFinish;
        this.duration = duration;
        time = 0f;
    }

}
