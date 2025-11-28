package kfs.golem;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import kfs.golem.comp.*;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.ecs.KfsWorld;
import kfs.golem.scenes.*;
import kfs.golem.sys.*;
import kfs.golem.utils.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class GolemMain extends ApplicationAdapter {

    public final KfsWorld world = new KfsWorld();
    public BubbleTextureGenerator bubbleTextureGenerator;
    public SceneLoader sceneCurrent = null;
    private SceneLoader sceneOld = null;
    private BitmapFont font36;
    private BitmapFont font24;

    @Override
    public void create() {
        Gdx.gl.glDisable(GL20.GL_BLEND);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        resize();
        font36 = new BitmapFont(Gdx.files.internal("fonts/MedievalSharp/MedievalSharp36.fnt"));
        font24 = new BitmapFont(Gdx.files.internal("fonts/MedievalSharp/MedievalSharp24.fnt"));
        bubbleTextureGenerator = new BubbleTextureGenerator(font24);

        world.addSys(new ClickSystem(this));
        world.addSys(new InteractiveSystem(this));
        world.addSys(new MusicSystem(this));
        world.addSys(new ParallaxSystem(this));
        world.addSys(new TimerSystem(this));
        world.addSys(new RenderSystem(this, font36, font24, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        world.addSys(new SubtitleMultilineSystem(this.world));
        world.addSys(new SubtitleSystem(this));
        ShaderProgram.pedantic = false;
        for (ShaderType st : ShaderType.values()) {
            if (st.shaderComponent != null) {
                try {
                    st.shaderComponent.getDeclaredMethod("register", KfsWorld.class).invoke(null, world);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        world.init();

        //loadScene(new C1S1_Prague(this), 1f);
        loadScene(new C1S5_AlchemystWorkShop(this), 1f);
    }

    @Override
    public void dispose() {
        super.dispose();

        for (Entity e : world.getEntitiesWith(PostEffectComponent.class)) {
            world.getComponent(e, PostEffectComponent.class).dispose();
        }
        for (Entity e : world.getEntitiesWith(ShaderEffectComponent.class)) {
            world.getComponent(e, ShaderEffectComponent.class).dispose();
        }

        world.reset();
        world.done();
        font36.dispose();
        font24.dispose();
    }

    public void resize() {
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void resize(int width, int height) {
        Optional.ofNullable(world.getSystem(RenderSystem.class))
            .ifPresent(a->a.resize(width, height));
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        world.update(deltaTime);
    }

    public void loadScene(SceneLoader sceneLoader, float fadeDuration) {
        if (sceneCurrent != null) {
            sceneOld = sceneCurrent;
        }
        sceneCurrent = sceneLoader;
        sceneCurrent.load();
        resize();
        Entity e = world.createEntity();
        world.addComponent(e, new CrossfadeComponent());
        world.addComponent(e, new TimerComponent(fadeDuration, () -> {
                if (sceneOld != null) {
                    sceneOld.unload();
                    sceneOld = null;
                }
            }));

        // reset time u efektu
        for (Entity ef: world.getEntitiesWith(PostEffectComponent.class)) {
            Optional.of(world.getComponent(ef, TimeComponent.class))
                .ifPresent(TimeComponent::rst);
        }
    }

}
