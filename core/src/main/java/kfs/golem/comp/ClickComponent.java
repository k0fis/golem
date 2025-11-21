package kfs.golem.comp;

import com.badlogic.gdx.math.Vector2;
import kfs.golem.ecs.KfsComp;

public class ClickComponent implements KfsComp {

    public final Vector2 click;

    public ClickComponent(Vector2 click) {
        this.click = click;
    }
    public ClickComponent(float x, float y) {
        this(new Vector2(x, y));
    }
}
