package kfs.golem.sys;

import com.badlogic.gdx.Gdx;
import kfs.golem.comp.SubtitleMultilineComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsSystem;
import kfs.golem.ecs.KfsWorld;

import java.util.List;

public class SubtitleMultilineSystem implements KfsSystem {

    private final KfsWorld world;

    public SubtitleMultilineSystem(KfsWorld world) {
        this.world = world;
    }

    @Override
    public void update(float delta) {
        for (Entity e : world.getEntitiesWith(SubtitleMultilineComponent.class)) {
            SubtitleMultilineComponent s = world.getComponent(e, SubtitleMultilineComponent.class);

            s.scrollOffset += s.scrollSpeed * delta;

            int lastVisibleIndex = (int)(s.scrollOffset / s.lineHeight);

            if (lastVisibleIndex > s.lines.size()) {
                s.endDuration -= delta;
                if (s.endDuration <= 0) {
                    if (s.onComplete != null) s.onComplete.run();
                    world.deleteEntity(e);
                }
            }
        }
    }
}
