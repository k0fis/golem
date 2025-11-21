package kfs.golem.comp;

import com.badlogic.gdx.math.Vector2;
import kfs.golem.ecs.KfsComp;

public class PositionComponent implements KfsComp {
    public Vector2 position;

    public PositionComponent(Vector2 position) {
        this.position = position;
    }
    public PositionComponent(float x, float y) {
        this(new Vector2(x, y));
    }
}
