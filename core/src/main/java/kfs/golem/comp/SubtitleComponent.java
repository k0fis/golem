package kfs.golem.comp;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import kfs.golem.ecs.KfsComp;

public class SubtitleComponent implements KfsComp {

    public Color color = Color.CLEAR_WHITE;
    public Vector2 position = new Vector2(80, 80);

    public float fadeIn = 1.1f;
    public float delay = 4f;
    public float fadeOut = 1.1f;

    public float time = 0;

    public String text;
    public Runnable onComplete;

    public SubtitleComponent(String text, Color color, Runnable onComplete) {
        this.text = text;
        this.color = color;
        this.onComplete = onComplete;
    }
}
