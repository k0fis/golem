package kfs.golem.test;

// LibGDX minimal demo: FBO + fullscreen quad + fade transition
// This is a single-file example (can be dropped into DesktopLauncher or Lwjgl3)
// You must integrate it into a real LibGDX project structure.

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.ScreenUtils;

public class DemFade extends ApplicationAdapter {
    SpriteBatch batch;
    FrameBuffer fbo;
    Texture demoTex;
    ShaderProgram fadeShader;
    float fade = 0f;      // 0 = full visible, 1 = fully black
    boolean fadingOut = true;

    @Override
    public void create() {
        batch = new SpriteBatch();
        demoTex = new Texture("images/prague_mid.png");

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
            false);

        // Shader: fade to black
        fadeShader = new ShaderProgram(
            Gdx.files.internal("shaders/fade.vert"),
            Gdx.files.internal("shaders/fade.frag")
        );

        if (!fadeShader.isCompiled())
            throw new RuntimeException(fadeShader.getLog());
    }

    @Override
    public void render() {
        // ---- 1) Render scene into FBO ----
        fbo.begin();
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setShader(null);
        batch.begin();
        batch.draw(demoTex, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        fbo.end();

        // ---- Update fade ----
        if (fadingOut) {
            fade += Gdx.graphics.getDeltaTime();
            if (fade >= 1f) { fade = 1f; fadingOut = false; }
        } else {
            fade -= Gdx.graphics.getDeltaTime();
            if (fade <= 0f) { fade = 0f; fadingOut = true; }
        }

        // ---- 2) Render FBO to screen with fade shader ----
        batch.setShader(null);
        batch.begin();

        ScreenUtils.clear(0,0,0,1);
        batch.setShader(fadeShader);
        fadeShader.setUniformf("u_alpha", fade);
        fadeShader.setUniformi("u_texture", 0);

        //Gdx.app.log("fade", fade+" alpha");

        fadeShader.bind();


        batch.draw(fbo.getColorBufferTexture(),
            0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
            0, 0, 1, 1);
        batch.end();

        batch.setShader(null);
    }
}
