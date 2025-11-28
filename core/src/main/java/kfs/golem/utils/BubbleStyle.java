package kfs.golem.utils;

import com.badlogic.gdx.graphics.Color;

public class BubbleStyle {

    public enum BubbleTail {
        NONE,
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    public Color color = Color.WHITE;
    public float padding = 18f;
    public float radius = 16f;        // jen pro NORMAL
    public float tailSize = 20f;
    public float maxWidth = 300f;
    public BubbleTail tail = BubbleTail.DOWN;

    public BubbleStyle() {
        this(BubbleTail.DOWN);
    }

    public BubbleStyle(BubbleTail tail) {
        this.tail = tail;
    }
}
