package kfs.golem.comp;

import kfs.golem.ecs.KfsComp;

public class InteractiveComponent implements KfsComp {
    public Runnable onInteract;
    public boolean isEnabled = true;
    public float radius = 64f; // zóna kliknutí

    public InteractiveComponent(Runnable onInteract) {
        this(onInteract, 64f);
    }
    public InteractiveComponent(Runnable onInteract, float radius) {
        this.onInteract = onInteract;
        this.radius = radius;
    }
}
