package kfs.golem.sys;

import kfs.golem.GolemMain;
import kfs.golem.comp.SubtitleComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsSystem;

public class SubtitleSystem implements KfsSystem {

    private final GolemMain golemMain;

    public SubtitleSystem(GolemMain golemMain) {
        this.golemMain = golemMain;
    }

    @Override
    public void update(float delta) {
        for (Entity subtitle : golemMain.world.getEntitiesWith(SubtitleComponent.class)) {
            SubtitleComponent t = golemMain.world.getComponent(subtitle, SubtitleComponent.class);
            t.time += delta;

            float total = t.fadeIn + t.delay + t.fadeOut;

            if (t.time < t.fadeIn) {
                // FADE IN
                t.color.a = t.time / t.fadeIn;

            } else if (t.time < t.fadeIn + t.delay) {
                // DELAY - full visibility
                t.color.a = 1f;

            } else if (t.time < total) {
                // FADE OUT
                float fadeOutTime = t.time - (t.fadeIn + t.delay);
                t.color.a = 1f - (fadeOutTime / t.fadeOut);

            } else {
                // DONE
                t.color.a = 0f;
                if (t.onComplete != null) {
                    t.onComplete.run();
                }
                golemMain.world.deleteEntity(subtitle);
            }
        }
    }

}
