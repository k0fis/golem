package kfs.golem.comp;

import com.badlogic.gdx.math.Vector2;
import kfs.golem.ecs.KfsComp;

public class SizeComponent implements KfsComp {
    public Vector2 size;

    public SizeComponent(Vector2 size) {
        this.size = size;
    }

    public SizeComponent(float width, float height) {
        this(new Vector2(width, height));
    }

    public float width() {
        return size.x;
    }

    public float height() {
        return size.y;
    }
}
