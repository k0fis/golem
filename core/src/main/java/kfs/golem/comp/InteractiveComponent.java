package kfs.golem.comp;

import com.badlogic.gdx.math.Vector2;
import kfs.golem.ecs.KfsComp;

public class InteractiveComponent implements KfsComp {
    public Runnable onInteract;
    public boolean isEnabled = true;
    public Vector2 size = new Vector2();

    public InteractiveComponent(Runnable onInteract, float width, float height) {
        this.onInteract = onInteract;
        this.size = new Vector2(width, height);
    }
}
