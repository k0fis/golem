package kfs.golem.comp;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import kfs.golem.ecs.KfsComp;
import kfs.golem.utils.BubbleStyle;

public class DialogComponent implements KfsComp {
    public final String text;
    public Color textColor = Color.BLACK;
    public BubbleStyle style = new BubbleStyle();
    public Vector2 size = new Vector2();

    public DialogComponent(String text) {
        this.text = text;
    }
}
