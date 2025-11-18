package kfs.golem.test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class DemoLight extends ApplicationAdapter {
    SpriteBatch batch;
    Texture bg;
    Texture white;
    ShaderProgram shader;

    Sprite lightSprite;
    Texture lightTex;

    float time = 0f;

    @Override
    public void create() {
        batch = new SpriteBatch();

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        white = new Texture(pm);

        bg = new Texture(Gdx.files.internal("images/prague_bridge.png"));

        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(
            Gdx.files.internal("lights/light.vert"),
            Gdx.files.internal("lights/light.frag")
        );
        if (!shader.isCompiled()) {
            Gdx.app.error("shader", shader.getLog());
        }

        lightTex = createRadialGradientTexture(256);
        lightSprite = new Sprite(lightTex);
        lightSprite.setOriginCenter();
        // měřítko sprite dle potřeby (nastav velikost světla)
        lightSprite.setSize(200, 200); // rozměr světla v pixelech
    }

    @Override
    public void dispose() {
        batch.dispose();
        shader.dispose();
        lightTex.dispose();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        time += delta;

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.08f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

// tady bys vykreslil svoji sceny (tilemapu, postavy, atd.)
        batch.begin();

        batch.draw(bg,
            0, 0,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight());

        batch.end();

// Vykreslíme světlo s additivním blendingem a vlastním shaderem
        batch.setShader(shader);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

// nastav uniformy shaderu
        shader.begin();
        shader.setUniformf("u_time", time);
        shader.setUniformf("u_lightColor", 1f, 0.92f, 0.65f);
        shader.setUniformf("u_intensity", .9f);
        shader.setUniformf("u_flickerSpeed", 80000.0f);
        shader.setUniformf("u_flickerStrength", 1.28f);
// elipsa: pokud je sprite širší než vysoký, scale.x > 1 => horizontální roztažení
        shader.setUniformf("u_ellipseScale", lightSprite.getWidth() / lightSprite.getHeight(), 1.5f);
        shader.end();

        batch.begin();
// pozice lucerny (např. kurzor nebo pevné souřadnice)
        float lampX = Gdx.graphics.getWidth() * 0.5f;
        float lampY = Gdx.graphics.getHeight() * 0.5f + 50f;
        lightSprite.setPosition(lampX - lightSprite.getWidth()/2f, lampY - lightSprite.getHeight()/2f);

// volitelně trochu měň měřítko pro vizuální percepci blikání
        float scaleJitter = 1.0f + 0.02f * (float)Math.sin(time * 14.0f + 1.3f);
        lightSprite.setScale(scaleJitter);
        lightSprite.draw(batch);

        lampX = 30;
        lampY = 30;
        lightSprite.setPosition(lampX - lightSprite.getWidth()/2f, lampY - lightSprite.getHeight()/2f);

// volitelně trochu měň měřítko pro vizuální percepci blikání
         scaleJitter = 1.0f + 0.035f * (float)Math.sin(time * 14.0f + 1.3f);
        lightSprite.setScale(scaleJitter);
        lightSprite.draw(batch);

        batch.end();

// reset shader a blending pro další kreslení
        batch.setShader(null);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    }

    private Texture createRadialGradientTexture(int size) {
        Pixmap p = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        int cx = size/2, cy = size/2;
        float maxd = (float)Math.hypot(cx, cy);
        for (int y=0; y<size; y++) {
            for (int x=0; x<size; x++) {
                float dx = x - cx;
                float dy = y - cy;
                float d = (float)Math.hypot(dx, dy) / (size/2f); // 0…~1.4
                float alpha = 1f - clamp(d, 0f, 1f); // lineární fade
// zjemnit křivkou pro hladší okraj
                alpha = (float)Math.pow(alpha, 1.4f);
                int a = (int)(alpha * 255f);
                int color = ((a & 0xff) << 24) | 0xffffff; // bílá s alfou
                p.drawPixel(x, y, color);
            }
        }
        Texture t = new Texture(p);
        p.dispose();
        return t;
    }

    private float clamp(float v, float a, float b) {
        return v < a ? a : (v > b ? b : v);
    }
}

