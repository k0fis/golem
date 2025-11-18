package kfs.golem.comp;

import com.badlogic.gdx.graphics.Color;
import kfs.golem.ecs.KfsComp;

public class LightComponent implements KfsComp {
    public float radius = 250;
    public float intensity = 1.0f;
    public Color color = new Color(1f, 0.9f, 0.7f, 1f);
}
