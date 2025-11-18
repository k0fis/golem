package kfs.golem;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import kfs.golem.comp.*;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsWorld;
import kfs.golem.scenes.C1S1_Prague;
import kfs.golem.scenes.SceneLoader;
import kfs.golem.shaders.FogEffect;
import kfs.golem.shaders.PaperEffect;
import kfs.golem.shaders.ShaderPipeline;
import kfs.golem.sys.*;

public class GolemMain extends ApplicationAdapter {

    public enum ShaderType {
        NONE, FOG, PAPER
    }

    public final KfsWorld world = new KfsWorld();
    private ShaderPipeline shaderPipeline;
    private SceneLoader currentScene = null;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;
    private SpriteBatch batch;

    @Override
    public void create() {
        stage = new Stage();
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(1200, 800,camera);
        shaderPipeline = new ShaderPipeline();
        shaderPipeline.add(new FogEffect(this));
        shaderPipeline.add(new PaperEffect(this));

        world.init();
        world.addSys(new AnimationSystem(this));
        world.addSys(new ClickSystem(this));
        world.addSys(new DialogueSystem(this));
        world.addSys(new FadeSystem(this));
        world.addSys(new InteractiveSystem(this));
        //world.addSys(new LightPulseSystem(this));
        //world.addSys(new LightRenderSystem(this));
        world.addSys(new MusicSystem(this));
        world.addSys(new ParallaxSystem(this));
        world.addSys(new RenderSystem(this));
        world.addSys(new SubtitleSystem(this));
        world.addSys(new TimerSystem(this));

        currentScene = new C1S1_Prague();
        currentScene.load(this);
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
        batch.dispose();
        world.reset();
        world.done();
        shaderPipeline.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public Vector2 getWorldSize() {
        return new Vector2(viewport.getWorldWidth(), viewport.getWorldHeight());
    }
    public Vector2 getGraphicsSize() {
        return new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        batch.begin();
        world.update(deltaTime);
        shaderPipeline.setShader(batch);
        world.render(batch);
        shaderPipeline.setUniforms(deltaTime);
        batch.end();

        batch.setShader(null);
    }


    public void loadScene(float duation, SceneLoader sceneLoader) {
        createFade(duation, true, () -> {
            if (currentScene != null) {
                currentScene.unload(this);
                currentScene = sceneLoader;
            }
            sceneLoader.load(this);
        });
    }

    public void createSubtitle(String text, Runnable onComplete) {
        world.addComponent(world.createEntity(), new SubtitleComponent(text, onComplete));
    }

    public Entity createLayer(String texturePath, float speedFactor) {
        Entity entity = world.createEntity();
        world.addComponent(entity, new TextureComponent(new TextureRegion(new Texture(Gdx.files.internal(texturePath))), true));
        world.addComponent(entity, new PositionComponent(0, 0));
        if (speedFactor > 0) {
            world.addComponent(entity, new ParallaxComponent(speedFactor, 5f));
        }
        return entity;
    }

    public Entity createLamp(String texturePath, float x, float y) {
        Entity entity = world.createEntity();
        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(new Texture("golem_idle_1.png")));
        frames.add(new TextureRegion(new Texture("golem_idle_2.png")));
        frames.add(new TextureRegion(new Texture("golem_idle_3.png")));

        AnimationComponent ac = new AnimationComponent(new Animation<>(0.25f, frames, Animation.PlayMode.LOOP), false);
        world.addComponent(entity, new TextureComponent(frames.first(), false));
        world.addComponent(entity, ac);
        world.addComponent(entity, new PositionComponent(x, y));
        world.addComponent(entity, new LightPulseComponent());
        world.addComponent(entity, new InteractiveComponent(() -> {
            ac.enabled = !ac.enabled;   // toggle
        }));
        return entity;
    }

    public Entity createGolem(float x, float y) {
        Entity entity = world.createEntity();

        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(new Texture("lamp_1.png")));
        frames.add(new TextureRegion(new Texture("lamp_2.png")));
        frames.add(new TextureRegion(new Texture("lamp_3.png")));
        frames.add(new TextureRegion(new Texture("lamp_2.png"))); // plynulé zpět

        world.addComponent(entity, new PositionComponent(x, y));
        world.addComponent(entity, new AnimationComponent(new Animation<>(0.15f, frames, Animation.PlayMode.LOOP), false));
        world.addComponent(entity, new TextureComponent(new TextureRegion(new Texture(Gdx.files.internal("characters/golem_idle.png"))), false));
        world.addComponent(entity, new GolemComponent());
        return entity;
    }

    public Entity createFade(float duration, boolean fadeIn, Runnable callback) {
        Entity e = world.createEntity();
        FadeComponent fade = new FadeComponent();
        fade.duration = duration;
        fade.mode = fadeIn ? FadeComponent.Mode.FADE_IN : FadeComponent.Mode.FADE_OUT;
        fade.onComplete = callback;
        world.addComponent(e, fade);
        return e;
    }
}
