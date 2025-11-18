package kfs.golem.comp;

import kfs.golem.ecs.KfsComp;

public class SoundComponent implements KfsComp {

    public final String soundPath;

    public SoundComponent(String soundPath) {
        this.soundPath = soundPath;
    }
}
