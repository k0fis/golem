package kfs.golem.comp;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import kfs.golem.ecs.KfsComp;

public class SubtitleComponent implements KfsComp {

    public Color color = Color.CLEAR_WHITE;
    public Vector2 position = new Vector2(80, 80);
    public float fontScale = 1.2f;

    public float fadeIn = 0.5f;
    public float delay = 15f;
    public float fadeOut = 0.5f;

    public float time = 0;
    public float alpha = 0f;

    public String text;
    public Runnable onComplete;

    public SubtitleComponent(String text, Runnable onComplete) {
        this.text = text;
        this.onComplete = onComplete;
    }
}
