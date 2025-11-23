package kfs.golem;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import kfs.golem.comp.*;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.ecs.KfsWorld;
import kfs.golem.scenes.C1S1_Prague;
import kfs.golem.scenes.SceneLoader;
import kfs.golem.scenes.ShaderType;
import kfs.golem.sys.*;
import kfs.golem.utils.*;

import java.util.*;

public class GolemMain extends ApplicationAdapter {


    public final KfsWorld world = new KfsWorld();
    private final Map<Class<? extends SceneLoader>, List<Class<? extends KfsComp>>> sceneShaders = new HashMap<>();
    public BubbleTextureGenerator bubbleTextureGenerator;
    private FrameBuffer fbCurrent, fbOld;
    private SceneLoader sceneCurrent = null;
    private SceneLoader sceneOld = null;
    private SpriteBatch batch;
    private SceneEntityFilter filterSceneCurrent;
    private SceneEntityFilter filterSceneOld;
    private BitmapFont font36;
    private BitmapFont font24;

    @Override
    public void create() {
        resize();
        batch = new SpriteBatch();
        font36 = new BitmapFont(Gdx.files.internal("fonts/MedievalSharp/MedievalSharp36.fnt"));
        font24 = new BitmapFont(Gdx.files.internal("fonts/MedievalSharp/MedievalSharp24.fnt"));
        bubbleTextureGenerator = new BubbleTextureGenerator(font24);

        world.addSys(new ClickSystem(this));
        world.addSys(new InteractiveSystem(this));
        world.addSys(new MusicSystem(this));
        world.addSys(new CrossfadeSystem(world));
        world.addSys(new ParallaxSystem(this));
        world.addSys(new TimerSystem(this));
        world.addSys(new RenderSystem(this, font36, font24));
        world.addSys(new SubtitleMultilineSystem(this.world));
        world.addSys(new SubtitleSystem(this));
        world.init();
        initShaders();

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
        font36.dispose();
        font24.dispose();

        for (Entity e : world.getEntitiesWith(PostEffectComponent.class)) {
            world.getComponent(e, PostEffectComponent.class).dispose();
        }
        for (Entity e : world.getEntitiesWith(ShaderEffectComponent.class)) {
            world.getComponent(e, ShaderEffectComponent.class).dispose();
        }
    }

    public void resize() {
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void resize(int width, int height) {
        if (fbCurrent != null) fbCurrent.dispose();
        if (fbOld != null) fbOld.dispose();

        fbCurrent = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        fbOld = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);

        for (Entity e : world.getEntitiesWith(PostEffectComponent.class)) {
            PostEffectComponent pec = world.getComponent(e, PostEffectComponent.class);
            if (pec.buffer != null) {
                pec.buffer.dispose();
            }
            pec.buffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        }
    }

    public Vector2 getWorldSize() {
        return new Vector2(fbCurrent.getWidth(), fbCurrent.getHeight());
    }


    private void initShaders() {
        ShaderProgram.pedantic = false;

        Entity fogEntity = world.createEntity();
        ShaderFogComponent sfc = new ShaderFogComponent(world);
        world.addComponent(fogEntity, sfc);
        world.addComponent(fogEntity, new TimeComponent());
        world.addComponent(fogEntity, new PostEffectComponent("shaders/fog.vert", "shaders/fog.frag", sfc));

        fogEntity = world.createEntity();
        ShaderFog2Component sf2c = new ShaderFog2Component(world);
        world.addComponent(fogEntity, sfc);
        world.addComponent(fogEntity, new TimeComponent());
        world.addComponent(fogEntity, new PostEffectComponent("shaders/fog.vert", "shaders/fog2.frag", sf2c));

        fogEntity = world.createEntity();
        ShaderFogSepiaSlideComponent sepia = new ShaderFogSepiaSlideComponent(world);
        world.addComponent(fogEntity, sepia);
        world.addComponent(fogEntity, new TimeComponent());
        world.addComponent(fogEntity, new PostEffectComponent("shaders/fog_sepia_slide.vert", "shaders/fog_sepia_slide.frag", sepia));

        fogEntity = world.createEntity();
        ShaderBrightenComponent brighten = new ShaderBrightenComponent(world);
        world.addComponent(fogEntity, brighten);
        world.addComponent(fogEntity, new TimeComponent());
        world.addComponent(fogEntity, new PostEffectComponent("shaders/brighten.vert", "shaders/brighten.frag", brighten));

        fogEntity = world.createEntity();
        ShaderLampComponent lamp = new ShaderLampComponent(world);
        world.addComponent(fogEntity, lamp);
        world.addComponent(fogEntity, new TimeComponent());
        world.addComponent(fogEntity, new ShaderEffectComponent("shaders/lamp.vert", "shaders/lamp.frag",
            new Texture(Gdx.files.internal("textures/lamp.png")), lamp));
    }

    private Texture render(FrameBuffer fb, SceneEntityFilter filterScene, float deltaTime) {
        fb.begin();
        batch.begin();
        world.getSystem(RenderSystem.class).render(batch, filterScene);
        batch.end();
        fb.end();

        Texture texture = fb.getColorBufferTexture();
        if (filterScene != null) {
            for (Class<? extends KfsComp> cls : sceneShaders.get(filterScene.filteringClass)) {
                for (Entity e : world.getEntitiesWith(PostEffectComponent.class, cls)) {
                    PostEffectComponent pec = world.getComponent(e, PostEffectComponent.class);
                    texture = pec.apply(batch, texture, e, deltaTime);
                }
            }
        }
        return texture;
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        world.update(deltaTime);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Texture texture1 = render(fbCurrent, filterSceneCurrent, deltaTime);
        int inx = 0;
        if (sceneOld != null) {
            Texture textureOld = render(fbOld, filterSceneOld, deltaTime);
            inx = world.getSystem(CrossfadeSystem.class).render(textureOld, texture1);
        }

        if (inx == 0) {
            batch.begin();
            batch.draw(texture1, 0f, 0f, fbCurrent.getWidth(), fbCurrent.getHeight(),
                0, 1, 1,0);
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

    public void setSceneShaders(Class<? extends SceneLoader> sceneLoaderCls, String []shaderNames) {
        List <Class<? extends KfsComp>> sceneShaderClasses = new ArrayList<>();
        if (shaderNames != null) {
            for (String name : shaderNames) {
                ShaderType type = ShaderType.fromShaderName(name);
                if (type != null) {
                    sceneShaderClasses.add(type.shaderComponent);
                }
            }
        }
        sceneShaders.put(sceneLoaderCls, sceneShaderClasses);
    }
}
