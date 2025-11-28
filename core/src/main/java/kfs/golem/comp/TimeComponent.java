package kfs.golem.comp;

import kfs.golem.ecs.KfsComp;

public class TimeComponent implements KfsComp {

    public float time = 0f;

    public void rst() {
        time = 0f;
    }
}
