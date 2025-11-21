package kfs.golem;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import kfs.golem.comp.*;
import kfs.golem.ecs.EntityFilter;
import kfs.golem.ecs.KfsWorld;
import kfs.golem.scenes.C1S1_Prague;
import kfs.golem.scenes.SceneLoader;
import kfs.golem.shaders.*;
import kfs.golem.sys.*;
import kfs.golem.utils.*;

public class GolemMain extends ApplicationAdapter {


    public final KfsWorld world = new KfsWorld();
    public BubbleTextureGenerator bubbleTextureGenerator;
    public ShaderPipeline shaderPipeline;
    private FrameBuffer fbCurrent, fbOld;
    private SceneLoader sceneCurrent = null;
    private SceneLoader sceneOld = null;
    private SpriteBatch batch;
    private EntityFilter filterSceneCurrent = EntityFilter.identity();
    private EntityFilter filterSceneOld;
    private BitmapFont font36;
    private BitmapFont font24;

    @Override
    public void create() {
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        shaderPipeline = new ShaderPipeline(this);
        font36 = new BitmapFont(Gdx.files.internal("fonts/MedievalSharp/MedievalSharp36.fnt"));
        font24 = new BitmapFont(Gdx.files.internal("fonts/MedievalSharp/MedievalSharp24.fnt"));
        bubbleTextureGenerator = new BubbleTextureGenerator(font24);

        world.addSys(new AnimationSystem(this));
        world.addSys(new ClickSystem(this));
        world.addSys(new InteractiveSystem(this));
        world.addSys(new MusicSystem(this));
        world.addSys(new CrossfadeSystem(this));
        world.addSys(new ParallaxSystem(this));
        world.addSys(new TimerSystem(this));
        world.addSys(new RenderSystem(this, font36, font24));
        world.addSys(new ShraderLocalSystem(this));
        world.addSys(new SubtitleMultilineSystem(this.world));
        world.addSys(new SubtitleSystem(this));
        world.init();

        loadScene(new C1S1_Prague(this));
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
        font36.dispose();
        font24.dispose();
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
        shaderPipeline.setShader(batch, filterSceneCurrent);
        batch.begin();
        world.getSystem(RenderSystem.class).render(batch, filterSceneCurrent);
        shaderPipeline.setUniforms(deltaTime, filterSceneCurrent);
        //batch.end();
        fbCurrent.end();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        int inx = 0;
        if (sceneOld != null) {
            fbOld.begin();
            shaderPipeline.setShader(batch, filterSceneOld);
            batch.begin();
            world.getSystem(RenderSystem.class).render(batch, filterSceneOld);
            shaderPipeline.setUniforms(deltaTime, filterSceneOld);
            //batch.end(); -> shrader in render ends transactions
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
        sceneLoader.load();
        sceneCurrent = sceneLoader;
        filterSceneCurrent = new SceneEntityFilter(world, sceneCurrent.getSLClass());
        world.addComponent(world.createEntity(), new CrossfadeComponent(3.5f, () -> {
            if (sceneOld != null) {
                sceneOld.unload();
                sceneOld = null;
            }
        }));
    }

}
