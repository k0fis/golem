package kfs.golem.comp;

import com.badlogic.gdx.math.Vector2;
import kfs.golem.ecs.KfsComp;

public class TransformComponent implements KfsComp {
    public Vector2 position = new Vector2();
    public float scaleX = 1f, scaleY = 1f;
    public float rotation = 0f;
    public float originX = 0f, originY = 0f;
}
