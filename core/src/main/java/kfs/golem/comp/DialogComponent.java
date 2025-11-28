package kfs.golem.comp;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import kfs.golem.ecs.KfsComp;
import kfs.golem.utils.BubbleStyle;

public class DialogComponent implements KfsComp {
    public final String text;
    public final BubbleStyle style;
    public Color textColor = Color.BLACK;
    public Vector2 size = new Vector2();

    public DialogComponent(String text, BubbleStyle style) {
        this.text = text;
        this.style = style;
    }
}
