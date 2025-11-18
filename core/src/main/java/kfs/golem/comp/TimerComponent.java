package kfs.golem.comp;

import kfs.golem.ecs.KfsComp;

public class TimerComponent implements KfsComp {

    public final float limit;
    public final Runnable action;
    public float time = 0f;
    public int count = 1;

    public TimerComponent(float limit, Runnable action) {
        this.limit = limit;
        this.action = action;
    }
}
