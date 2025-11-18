package kfs.golem.comp;

import com.badlogic.gdx.math.Vector2;
import kfs.golem.ecs.KfsComp;

public class PositionComponent implements KfsComp {
    public Vector2 position;
    public PositionComponent(float x, float y) {
        this.position = new Vector2(x, y);
    }
}
