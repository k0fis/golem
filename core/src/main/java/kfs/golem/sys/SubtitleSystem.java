package kfs.golem.sys;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import kfs.golem.GolemMain;
import kfs.golem.comp.SubtitleComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsSystem;

public class SubtitleSystem implements KfsSystem {

    private final BitmapFont font;
    private final GolemMain golemMain;

    public SubtitleSystem(GolemMain golemMain) {
        this.golemMain = golemMain;
        font = new BitmapFont();
    }

    @Override
    public void update(float delta) {
        for (Entity subtitle : golemMain.world.getEntitiesWith(SubtitleComponent.class)) {
            SubtitleComponent t = golemMain.world.getComponent(subtitle, SubtitleComponent.class);
            t.time += delta;

            float total = t.fadeIn + t.delay + t.fadeOut;

            if (t.time < t.fadeIn) {
                // FADE IN
                t.alpha = t.time / t.fadeIn;

            } else if (t.time < t.fadeIn + t.delay) {
                // DELAY - full visibility
                t.alpha = 1f;

            } else if (t.time < total) {
                // FADE OUT
                float fadeOutTime = t.time - (t.fadeIn + t.delay);
                t.alpha = 1f - (fadeOutTime / t.fadeOut);

            } else {
                // DONE
                t.alpha = 0f;
                golemMain.world.deleteEntity(subtitle);
                t.onComplete.run();
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        for (Entity subtitle : golemMain.world.getEntitiesWith(SubtitleComponent.class)) {
            SubtitleComponent sc = golemMain.world.getComponent(subtitle, SubtitleComponent.class);

            font.setColor(1f, 1f, 1f, sc.alpha);
            font.draw(batch, sc.text, sc.position.x, sc.position.y);
        }

    }
}
