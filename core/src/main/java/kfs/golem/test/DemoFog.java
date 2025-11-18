package kfs.golem.test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class DemoFog extends ApplicationAdapter {
    SpriteBatch batch;
    TextureRegion bg;
    ShaderProgram shader;
    Viewport viewport;
    float time = 0f;

    @Override
    public void create() {
        batch = new SpriteBatch();
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(1200, 800,camera);

        bg = new TextureRegion(new Texture(Gdx.files.internal("images/prague_mid.png")));

        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(
            Gdx.files.internal("shaders/fog.vert"),
            Gdx.files.internal("shaders/fog.frag")
        );


        if (!shader.isCompiled()) {
            Gdx.app.error("shader", shader.getLog());
        }
    }

    @Override
    public void render() {
        time += Gdx.graphics.getDeltaTime();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setShader(shader);
        batch.begin();

        shader.setUniformf("u_resolution",
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight());

        shader.setUniformf("u_time", time);

        batch.draw(bg.getTexture(),
            0, 0,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight());

        batch.end();

        batch.setShader(null);
    }
}

