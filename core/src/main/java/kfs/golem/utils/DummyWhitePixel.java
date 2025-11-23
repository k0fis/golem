package kfs.golem.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class DummyWhitePixel {

    public static Texture getWhiteTexture() {
        return getMonoTexture(1,1, 1, 1, 1, 1);
    }

    public static Texture getTransparentTexture() {
        return getMonoTexture(1,1, 1, 1, 1, 0);
    }

    public static Texture getMonoTexture(int width, int height, int r, int g, int b, int a) {
        Pixmap dummyWhitePixel = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        dummyWhitePixel.setColor(r, g, b, a);
        dummyWhitePixel.fill();
        Texture dummyWhiteTexture = new Texture(dummyWhitePixel);
        dummyWhitePixel.dispose();
        return dummyWhiteTexture;
    }
}
