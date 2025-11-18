package kfs.golem.comp;

import kfs.golem.ecs.KfsComp;

public class MusicComponent implements KfsComp {

    public final String musicPath;

    public MusicComponent(String musicPath) {
        this.musicPath = musicPath;
    }
}
