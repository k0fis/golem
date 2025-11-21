package kfs.golem.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import kfs.golem.GolemMain;
import kfs.golem.comp.*;
import kfs.golem.ecs.Entity;
import kfs.golem.scenes.data.BackgroundData;
import kfs.golem.scenes.data.SceneData;
import kfs.golem.scenes.data.SubtitleData;
import kfs.golem.scenes.data.TextureData;
import kfs.golem.shaders.ShaderType;
import kfs.golem.utils.BubbleStyle;

import java.util.*;

public abstract class SceneLoader {

    protected final GolemMain engine;
    protected final List<Entity> entities = new ArrayList<>();
    protected final SceneData _sceneData;

    protected Entity bgEntity;
    protected Entity subtitleEntity;
    protected Map<String, Entity> textures;


    protected SceneLoader(GolemMain engine, String path) {
        this.engine = engine;
        this.textures = new HashMap<>();
        this._sceneData = loadSceneData(path);
    }

    private static SceneData loadSceneData(String path) {
        if (path != null) {
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            try {
                return json.fromJson(SceneData.class, Gdx.files.internal(path));
            } catch (Exception e) {
                Gdx.app.error("SceneLoader", "Failed to load scene " + path, e);
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private Color getColor(String hex) {
        if (hex == null) return Color.WHITE;
        try {
            return Color.valueOf(hex);
        } catch (Exception e) {
            return Color.WHITE;
        }
    }

    public void load() {
        if (_sceneData == null) return;
        createBackground(_sceneData.background);
        createSubtitle(_sceneData.subtitle);
        createSceneShader(_sceneData.shader);
        if (_sceneData.textures != null) {
            for (TextureData texture : _sceneData.textures) {
                createTexture(texture);
            }
        }
    }

    private void createBackground(BackgroundData bg){
        if (bg == null) return;
        bgEntity = engine.world.createEntity();
        entities.add(bgEntity);
        engine.world.addComponent(bgEntity, new TextureComponent(new Texture(Gdx.files.internal(bg.file)), true));
        engine.world.addComponent(bgEntity, new PositionComponent(0, 0));
        engine.world.addComponent(bgEntity, new SceneIdComponent(getSLClass()));
        if (bg.parallax_speedFactor > 0) {
            engine.world.addComponent(bgEntity, new ParallaxComponent(bg.parallax_speedFactor, bg.amplitude));
        }
    }

    private void createSubtitle(SubtitleData subtitle) {
        if (subtitle == null) return;
        if (subtitle.initDelay > 0) {
            engine.world.addComponent(engine.world.createEntity(), new TimerComponent(subtitle.initDelay,
                ()->loadDialog(subtitle)));
        } else {
            loadDialog(subtitle);
        }
    }

    private void loadDialog(SubtitleData subtitle) {
        subtitleEntity = engine.world.createEntity();
        engine.world.addComponent(subtitleEntity, new SceneIdComponent(getSLClass()));

        if (subtitle.text.length == 1) {
            engine.world.addComponent(subtitleEntity, new SubtitleComponent(subtitle.text[0], getColor(subtitle.textColor), null));
        } else {
            SubtitleMultilineComponent s = new SubtitleMultilineComponent(Arrays.asList(subtitle.text), getColor(subtitle.textColor), null);
            s.maxLines = subtitle.maxLines;
            s.scrollSpeed = subtitle.scrollSpeed;
            s.endDuration = subtitle.endDuration;
            engine.world.addComponent(subtitleEntity, s);
        }
    }

    private void createSceneShader(String shaderName) {
        if (shaderName == null || shaderName.isEmpty()) return;
        Entity shaderEntity = bgEntity;
        if (shaderEntity == null) {
            shaderEntity =  engine.world.createEntity();
            engine.world.addComponent(shaderEntity, new SceneIdComponent(getSLClass()));
        }
        engine.world.addComponent(shaderEntity,
            new ShaderComponent(shaderName, bgEntity));
    }

    private void createTexture(TextureData textureData) {
        Entity textureEntity = engine.world.createEntity();
        textures.put(textureData.id, textureEntity);
        engine.world.addComponent(textureEntity, new SceneIdComponent(getSLClass()));
        engine.world.addComponent(textureEntity, new TextureComponent(new Texture(Gdx.files.internal(textureData.path)), false));
        engine.world.addComponent(textureEntity, new PositionComponent(textureData.posX, textureData.posY));
        engine.world.addComponent(textureEntity, new SizeComponent(textureData.width, textureData.height));
        engine.world.addComponent(textureEntity, new ShaderLocalComponent(textureData.shader));
    }

    public abstract Class<? extends SceneLoader> getSLClass();

    public void unload() {
        // release textures
        if (bgEntity != null) {
            disposeTexture(bgEntity);
        }
        for (Entity entity : textures.values()) {
            disposeTexture(entity);
        }
        for (Entity entity : entities) {
            engine.world.deleteEntity(entity);
        }
    }

    private void disposeTexture(Entity entity) {
        TextureComponent t = engine.world.getComponent(entity, TextureComponent.class);
        if (t != null) {
            t.texture.dispose();
            t.texture = null;
        }
    }

    public Entity createDialog(String text, Vector2 position, Runnable onClick, BubbleStyle.BubbleTail bStyle) {
        Entity entity = engine.world.createEntity();
        engine.world.addComponent(entity, new SceneIdComponent(getSLClass()));
        DialogComponent dc = new DialogComponent(text);
        dc.style.tail = bStyle;
        engine.world.addComponent(entity, dc);
        Texture t = engine.bubbleTextureGenerator.generateBubble(text, dc.style, dc.size);
        engine.world.addComponent(entity, new PositionComponent(position));
        engine.world.addComponent(entity, new TextureComponent(t, false));
        engine.world.addComponent(entity, new InteractiveComponent( ()->{
            if (onClick != null) onClick.run();
            engine.world.addComponent(engine.world.createEntity(), new TimerComponent(3f, () ->{
                engine.world.deleteEntity(entity);
                t.dispose();
            }));
        }, t.getWidth(), t.getHeight()));
        return entity;
    }

    protected void createTimeAfterSubtitlesForNextScene(float subtitleDelay, SceneLoader nextScene) {
        Entity timer = engine.world.createEntity();
        engine.world.addComponent(timer, new TimerComponent(subtitleDelay,  ()-> {
            SubtitleMultilineComponent c = engine.world.getComponent(subtitleEntity, SubtitleMultilineComponent.class);
            if (c != null) c.onComplete = () -> engine.loadScene(nextScene);
        }));
        engine.world.addComponent(timer, new SceneIdComponent(getSLClass()));
        entities.add(timer);
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
