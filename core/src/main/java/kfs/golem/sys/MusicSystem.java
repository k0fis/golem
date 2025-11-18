package kfs.golem.sys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import kfs.golem.GolemMain;
import kfs.golem.comp.MusicComponent;
import kfs.golem.comp.SoundComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsSystem;

import java.util.HashMap;
import java.util.Map;

public class MusicSystem implements KfsSystem {

    private final GolemMain golem;

    Map<String, Sound> sounds = new HashMap<>();
    Map<String, Music> musics = new HashMap<>();

    public MusicSystem(GolemMain golem) {
        this.golem = golem;
    }

    @Override
    public void done() {
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
        for (Music music : musics.values()) {
            music.dispose();
        }
    }

    @Override
    public void update(float delta) {
        for (Entity soundEndity : golem.world.getEntitiesWith(SoundComponent.class)) {
            SoundComponent sc = golem.world.getComponent(soundEndity, SoundComponent.class);

            if (sounds.get(sc.soundPath) == null) {
                try {
                    sounds.put(sc.soundPath, Gdx.audio.newSound(Gdx.files.internal(sc.soundPath)));
                } catch (Exception e) {
                    Gdx.app.error("MusicSystem", "Error loading sound" + sc.soundPath, e);
                }
            }
            Sound sound = sounds.get(sc.soundPath);
            golem.world.deleteEntity(soundEndity);
            if (sound != null) {
                sound.play();
            }
        }
        for (Entity musicEntity : golem.world.getEntitiesWith(MusicComponent.class)) {
            MusicComponent mc = golem.world.getComponent(musicEntity, MusicComponent.class);

            if (musics.get(mc.musicPath) == null) {
                try {
                    musics.put(mc.musicPath, Gdx.audio.newMusic(Gdx.files.internal(mc.musicPath)));
                } catch (Exception e) {
                    Gdx.app.error("MusicSystem", "Error loading music" + mc.musicPath, e);
                }
            }

            Music music = musics.get(mc.musicPath);
            golem.world.deleteEntity(musicEntity);
            if (music != null) {
                music.play();
            }
        }
    }
}
