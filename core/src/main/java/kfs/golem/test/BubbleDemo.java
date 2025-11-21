package kfs.golem.test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

public class BubbleDemo extends ApplicationAdapter {

    enum TailDirection { LEFT, RIGHT, DOWN }

    SpriteBatch batch;
    BitmapFont font;
    Texture bubbleTex;
    GlyphLayout layout;

    int bubbleWidth;
    int bubbleHeight;
    int padding = 20;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("fonts/MedievalSharp/MedievalSharp24.fnt"));

        String text =
            "Tohle je ukázková dialogová bublina.\n" +
                "Podporuje víc řádků i zalomení textu.";

        int maxWidth = 300;

        // připrav layout (wrap)
        layout = new GlyphLayout();
        layout.setText(font, text, Color.BLACK, maxWidth, Align.left, true);

        bubbleWidth = (int)layout.width + padding * 2;
        bubbleHeight = (int)layout.height + padding * 2;

        // vytvoříme texturu bubliny
        bubbleTex = generateBubbleTexture(
            (int)layout.width,
            (int)layout.height,
            TailDirection.RIGHT
        );
    }

    @Override
    public void render() {

        Gdx.gl.glClearColor(0.12f, 0.12f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        int x = 200;
        int y = 200;

        // vykreslení bubliny
        batch.draw(bubbleTex, x, y);

        // vykreslení textu
        font.setColor(Color.BLACK);
        font.draw(batch, layout,
            x + padding,
            y + bubbleHeight - padding
        );

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        bubbleTex.dispose();
    }


    // ------------------------------------------------------------------------
    //   BUBBLE GENERATOR
    // ------------------------------------------------------------------------

    Texture generateBubbleTexture(int textWidth, int textHeight, TailDirection dir) {

        int tailSize = 20;

        int bw = textWidth + padding * 2;
        int bh = textHeight + padding * 2;

        int totalW = bw;
        int totalH = bh;

        switch (dir) {
            case DOWN: totalH += tailSize; break;
            case LEFT:
            case RIGHT: totalW += tailSize; break;
        }

        Pixmap pix = new Pixmap(totalW, totalH, Pixmap.Format.RGBA8888);

        pix.setColor(0, 0, 0, 0);
        pix.fill();

        int ox = (dir == TailDirection.LEFT) ? tailSize : 0;
        int oy = (dir == TailDirection.DOWN) ? tailSize : 0;

        pix.setColor(Color.WHITE);

        fillRoundedRect(pix, ox, oy, bw, bh, 18);

        switch (dir) {
            case DOWN:
                pix.fillTriangle(
                    ox + bw/2 - tailSize,
                    oy,
                    ox + bw/2 + tailSize,
                    oy,
                    ox + bw/2,
                    0
                );
                break;

            case LEFT:
                pix.fillTriangle(
                    ox,
                    oy + bh/2 - tailSize,
                    ox,
                    oy + bh/2 + tailSize,
                    0,
                    oy + bh/2
                );
                break;

            case RIGHT:
                pix.fillTriangle(
                    ox + bw,
                    oy + bh/2 - tailSize,
                    ox + bw,
                    oy + bh/2 + tailSize,
                    ox + bw + tailSize,
                    oy + bh/2
                );
                break;
        }

        Texture t = new Texture(pix);
        pix.dispose();
        return t;
    }


    // ---------------- Rounded Rect + Circle --------------------

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
