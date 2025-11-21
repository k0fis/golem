package kfs.golem.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

public class BubbleTextureGenerator {

    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    public BubbleTextureGenerator(BitmapFont font) {
        this.font = font;
    }

    public Texture generateBubble(String text, BubbleStyle style, Vector2 size) {

        layout.setText(font, text, Color.WHITE, style.maxWidth, Align.left, true);

        size.x = layout.width + style.padding * 2;
        size.y = layout.height + style.padding * 2;

        Pixmap pix = new Pixmap(
            (int)(size.x+ ((style.tail == BubbleStyle.BubbleTail.LEFT || style.tail == BubbleStyle.BubbleTail.RIGHT) ? style.tailSize : 0)),
            (int)(size.y+ ((style.tail == BubbleStyle.BubbleTail.UP || style.tail == BubbleStyle.BubbleTail.DOWN) ? style.tailSize : 0)),
            Pixmap.Format.RGBA8888);

        pix.setColor(0, 0, 0, 0);
        pix.fill();

        int ox = (style.tail == BubbleStyle.BubbleTail.LEFT) ? (int)style.tailSize : 0;
        int oy = (style.tail == BubbleStyle.BubbleTail.UP) ? (int)style.tailSize : 0;

        style.color.a = 1;
        pix.setColor(style.color);
        fillRoundedRect(pix, ox, oy, (int)size.x, (int)size.y, (int)style.radius);

        switch (style.tail) {
            case UP:
                pix.fillTriangle(
                    (int)(ox + size.x/2 - style.tailSize),
                    oy,
                    (int)(ox + size.x/2 + style.tailSize),
                    oy,
                    (int)(ox + size.x/2),
                    0
                );
                break;
            case DOWN:
                pix.fillTriangle(
                    (int)(ox + size.x/2 - style.tailSize),
                    (int)(size.y),
                    (int)(ox + size.x/2 + style.tailSize),
                    (int)(size.y),
                    (int)(ox + size.x/2),
                    (int)(size.y + style.tailSize)
                );
                break;
            case LEFT:
                pix.fillTriangle(
                    ox,
                    (int)(oy + size.y/2 - style.tailSize),
                    ox,
                    (int)(oy + size.y/2 + style.tailSize),
                    0,
                    (int)(oy + size.y/2)
                );
                break;

            case RIGHT:
                pix.fillTriangle(
                    (int)(ox + size.x),
                    (int)(oy + size.y/2 - style.tailSize),
                    (int)(ox + size.x),
                    (int)(oy + size.y/2 + style.tailSize),
                    (int)(ox + size.x + style.tailSize),
                    (int)(oy + size.y/2)
                );
                break;
        }

        Texture t = new Texture(pix);
        pix.dispose();
        return t;
    }

    void fillRoundedRect(Pixmap pix, int x, int y, int w, int h, int r) {
        pix.fillRectangle(x + r, y, w - 2*r, h);
        pix.fillRectangle(x, y + r, w, h - 2*r);

        drawFilledCircle(pix, x + r, y + r, r);
        drawFilledCircle(pix, x + w - r - 1, y + r, r);
        drawFilledCircle(pix, x + r, y + h - r - 1, r);
        drawFilledCircle(pix, x + w - r - 1, y + h - r - 1, r);
    }

    void drawFilledCircle(Pixmap pix, int cx, int cy, int r) {
        for (int dy = -r; dy <= r; dy++) {
            for (int dx = -r; dx <= r; dx++) {
                if (dx*dx + dy*dy <= r*r) {
                    pix.drawPixel(cx + dx, cy + dy);
                }
            }
        }
    }
}

