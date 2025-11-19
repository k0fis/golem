package kfs.golem.ecs;

public interface EntityFilter {

    default boolean filter(Entity entity) {
        return true;
    }

    public static EntityFilter identity() {
        return new EntityFilter() {};
    }
}
