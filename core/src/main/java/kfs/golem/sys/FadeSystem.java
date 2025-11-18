package kfs.golem.sys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import kfs.golem.GolemMain;
import kfs.golem.comp.FadeComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsSystem;

public class FadeSystem implements KfsSystem {

    public final GolemMain golemMain;
    private final Pixmap whitePm;
    private Texture whitePixel;

    public FadeSystem(GolemMain golemMain) {
        this.golemMain = golemMain;

        whitePm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePm.setColor(Color.WHITE);
        whitePm.fill();
        whitePixel = new Texture(whitePm);
    }

    @Override
    public void update(float delta) {
        for (Entity fade : golemMain.world.getEntitiesWith(FadeComponent.class)) {
            FadeComponent f = golemMain.world.getComponent(fade, FadeComponent.class);
            if (f.mode == FadeComponent.Mode.NONE) return;
            f.time += delta;
            float t = Math.min(f.time / f.duration, 1f);

            switch (f.mode) {
                case FADE_IN:
                    f.alpha = 1f - t;
                    break;
                case FADE_OUT:
                    f.alpha = t;
                    break;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        for (Entity fade : golemMain.world.getEntitiesWith(FadeComponent.class)) {
            FadeComponent f = golemMain.world.getComponent(fade, FadeComponent.class);
            batch.setColor(0, 0, 0, f.alpha);
            batch.draw(whitePixel, 0, 0,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());
            batch.setColor(1, 1, 1, 1);

            // dokončení
            if (f.time >= 1f) {
                FadeComponent.Mode mode = f.mode;
                f.mode = FadeComponent.Mode.NONE;

                if (f.onComplete != null) {
                    f.onComplete.run();
                }
            }
        }
    }

    @Override
    public void done() {
        whitePixel.dispose();
        whitePm.dispose();
    }
}
