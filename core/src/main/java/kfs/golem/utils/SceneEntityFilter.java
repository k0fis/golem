package kfs.golem.utils;

import kfs.golem.comp.SceneIdComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.EntityFilter;
import kfs.golem.ecs.KfsWorld;
import kfs.golem.scenes.SceneLoader;

public class SceneEntityFilter implements EntityFilter {

    private final KfsWorld world;
    private final Class<? extends SceneLoader> filteringClass;

    public SceneEntityFilter(KfsWorld world, Class<? extends SceneLoader> filteringClass) {
        this.world = world;
        this.filteringClass = filteringClass;
    }

    @Override
    public boolean filter(Entity entity) {
        SceneIdComponent sid = world.getComponent(entity, SceneIdComponent.class);
        return sid != null && sid.sceneIdClass.equals(filteringClass);
    }
}
