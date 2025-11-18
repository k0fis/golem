package kfs.golem.comp;

import com.badlogic.gdx.math.Vector2;
import kfs.golem.ecs.KfsComp;

public class ClickComponent implements KfsComp {

    public final Vector2 click;

    public ClickComponent(float x, float y) {
        click = new Vector2(x, y);
    }
}
