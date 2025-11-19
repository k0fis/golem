package kfs.golem;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import kfs.golem.comp.*;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.EntityFilter;
import kfs.golem.ecs.KfsWorld;
import kfs.golem.scenes.C1S1_Prague;
import kfs.golem.scenes.SceneLoader;
import kfs.golem.shaders.*;
import kfs.golem.sys.*;
import kfs.golem.utils.SceneEntityFilter;

public class GolemMain extends ApplicationAdapter {

    public enum ShaderType {
        NONE, CROSS_FADE, FOG, FOG_SEPIA_SLIDE
    }

    public final KfsWorld world = new KfsWorld();
    private FrameBuffer fbCurrent, fbOld;
    private ShaderPipeline shaderPipeline;
    private SceneLoader sceneCurrent = null;
    private SceneLoader sceneOld = null;
    private SpriteBatch batch;
    private EntityFilter filterSceneCurrent = EntityFilter.identity();
    private EntityFilter filterSceneOld;
    private BitmapFont font36;

    @Override
    public void create() {
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        shaderPipeline = new ShaderPipeline();
        shaderPipeline.add(new FogEffect(this));
        shaderPipeline.add(new FogSepiaSlideEffect(this));
        font36 = new BitmapFont(Gdx.files.internal("fonts/MedievalSharp/MedievalSharp36.fnt"));

        world.addSys(new AnimationSystem(this));
        world.addSys(new ClickSystem(this));
        world.addSys(new DialogueSystem(this));
        world.addSys(new InteractiveSystem(this));
        world.addSys(new MusicSystem(this));
        world.addSys(new CrossfadeSystem(this));
        world.addSys(new ParallaxSystem(this));
        world.addSys(new TimerSystem(this));
        world.addSys(new RenderSystem(this, font36));
        world.addSys(new SubtitleSystem(this));
        world.init();

        loadScene(new C1S1_Prague());
    }

    @Override
    public void dispose() {
        super.dispose();
        if (fbOld != null) fbOld.dispose();
        if (fbCurrent != null) fbCurrent.dispose();
        batch.dispose();
        world.reset();
        world.done();
        shaderPipeline.dispose();
    }

    @Override
    public void resize(int width, int height) {
        if (fbCurrent != null) fbCurrent.dispose();
        if (fbOld != null) fbOld.dispose();

        fbCurrent = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        fbOld = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
    }

    public Vector2 getWorldSize() {
        return new Vector2(fbCurrent.getWidth(), fbCurrent.getHeight());
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        world.update(deltaTime);

        fbCurrent.begin();
        batch.begin();
        shaderPipeline.setShader(batch, filterSceneCurrent);
        world.getSystem(RenderSystem.class).render(batch, filterSceneCurrent);
        shaderPipeline.setUniforms(deltaTime, filterSceneCurrent);
        batch.end();
        fbCurrent.end();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        int inx = 0;
        if (sceneOld != null) {
            fbOld.begin();
            batch.begin();
            shaderPipeline.setShader(batch, filterSceneOld);
            world.getSystem(RenderSystem.class).render(batch, filterSceneOld);
            shaderPipeline.setUniforms(deltaTime, filterSceneOld);
            batch.end();
            fbOld.end();

            inx = world.getSystem(CrossfadeSystem.class).render(batch, fbOld, fbCurrent);
        }

        if (inx == 0) {
            batch.begin();
            batch.draw(fbCurrent.getColorBufferTexture(), 0f, 0f, fbCurrent.getWidth(), fbCurrent.getHeight(),
                0, 0, 1,1);
            batch.end();
        }

        batch.setShader(null);
    }


    public void loadScene(SceneLoader sceneLoader) {
        if (sceneCurrent != null) {
            sceneOld = sceneCurrent;
            filterSceneOld = filterSceneCurrent;
        }
        sceneCurrent = sceneLoader;
        filterSceneCurrent = new SceneEntityFilter(world, sceneCurrent.getSLClass());
        sceneCurrent.load(this);
        world.addComponent(world.createEntity(), new CrossfadeComponent(3.5f, () -> {
            if (sceneOld != null) {
                sceneOld.unload(this);
                sceneOld = null;
            }
        }));
    }

    public Entity createSubtitle(String text, Color color, Runnable onComplete) {
        Entity e = world.createEntity();
        world.addComponent(e, new SubtitleComponent(text, color, onComplete));
        return e;
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
/*
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
*/
}
