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

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class GolemMain extends ApplicationAdapter {


    public final KfsWorld world = new KfsWorld();
    private final Map<Class<? extends SceneLoader>, List<Class<? extends KfsComp>>> sceneShaders = new HashMap<>();
    public BubbleTextureGenerator bubbleTextureGenerator;
    private FrameBuffer fbCurrent;
    private SceneLoader sceneCurrent = null;
    private SceneLoader sceneOld = null;
    public SceneEntityFilter filterSceneCurrent;
    private BitmapFont font36;
    private BitmapFont font24;

    @Override
    public void create() {
        resize();
        font36 = new BitmapFont(Gdx.files.internal("fonts/MedievalSharp/MedievalSharp36.fnt"));
        font24 = new BitmapFont(Gdx.files.internal("fonts/MedievalSharp/MedievalSharp24.fnt"));
        bubbleTextureGenerator = new BubbleTextureGenerator(font24);

        world.addSys(new ClickSystem(this));
        world.addSys(new InteractiveSystem(this));
        world.addSys(new MusicSystem(this));
        world.addSys(new ParallaxSystem(this));
        world.addSys(new TimerSystem(this));
        world.addSys(new RenderSystem(this, font36, font24));
        world.addSys(new SubtitleMultilineSystem(this.world));
        world.addSys(new SubtitleSystem(this));
        ShaderProgram.pedantic = false;
        for (ShaderType st : ShaderType.values()) {
            try {
                if (st.shaderComponent != null)
                st.shaderComponent.getDeclaredMethod("register", KfsWorld.class).invoke(null, world);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        world.init();

        loadScene(new C1S1_Prague(this), 1f);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (fbCurrent != null) fbCurrent.dispose();
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
        fbCurrent = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);

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

    }

    private Texture render(FrameBuffer fb, SceneEntityFilter filterScene, float deltaTime) {
        SpriteBatch batch = new SpriteBatch();
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
        batch.setShader(null);
        batch.dispose();
        return texture;
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        world.update(deltaTime);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Texture texture1 = render(fbCurrent, filterSceneCurrent, deltaTime);

        SpriteBatch batch = new SpriteBatch();
        batch.begin();
        Optional<Entity> oe = world.getEntityWith(CrossfadeComponent.class);
        if (oe.isPresent()){
            TimerComponent timerComp = world.getComponent(oe.get(), TimerComponent.class);
            batch.setColor(1, 1, 1, timerComp.time / timerComp.limit);
        }
        batch.draw(texture1, 0f, 0f, fbCurrent.getWidth(), fbCurrent.getHeight(),
            0, 0, 1, 1);
        world.getSystem(RenderSystem.class).renderText(batch, filterSceneCurrent);
        batch.end();
        batch.dispose();
    }

    public void loadScene(SceneLoader sceneLoader, float fadeDuration) {
        if (sceneCurrent != null) {
            sceneOld = sceneCurrent;
        }
        sceneCurrent = sceneLoader;
        filterSceneCurrent = new SceneEntityFilter(world, sceneCurrent.getSLClass());
        sceneCurrent.load();

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
            TimeComponent tc = world.getComponent(ef, TimeComponent.class);
            if (tc != null) {
                tc.time = 0f;
            }
        }
    }

    public void setSceneShaders(Class<? extends SceneLoader> sceneLoaderCls, String []shaderNames) {
        List <Class<? extends KfsComp>> sceneShaderClasses = new ArrayList<>();
        if (shaderNames != null) {
            for (String name : shaderNames) {
                ShaderType type = ShaderType.fromShaderName(name);
                if (type != ShaderType.NONE) {
                    sceneShaderClasses.add(type.shaderComponent);
                }
            }
        }
        sceneShaders.put(sceneLoaderCls, sceneShaderClasses);
    }

    public void addSceneShader(Class<? extends SceneLoader> sceneLoaderCls, ShaderType shaderType) {
        List <Class<? extends KfsComp>> sceneShaderClasses = sceneShaders.computeIfAbsent(sceneLoaderCls, k->new ArrayList<>());
        sceneShaderClasses.add(shaderType.shaderComponent);
    }

    public void removeSceneShader(Class<? extends SceneLoader> sceneLoaderCls, ShaderType shaderType) {
        List <Class<? extends KfsComp>> sceneShaderClasses = sceneShaders.computeIfAbsent(sceneLoaderCls, k->new ArrayList<>());
        sceneShaderClasses.remove(shaderType.shaderComponent);
    }
}
