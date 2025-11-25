package kfs.golem.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import kfs.golem.GolemMain;
import kfs.golem.comp.*;
import kfs.golem.ecs.Entity;
import kfs.golem.scenes.data.*;
import kfs.golem.utils.BubbleStyle;
import kfs.golem.utils.InvalidShaderName;
import kfs.golem.utils.SceneLoaderException;

import java.util.*;

public abstract class SceneLoader {

    protected final GolemMain golemMain;
    protected final List<Entity> entities = new ArrayList<>();
    protected final SceneData _sceneData;

    protected Entity bgEntity;
    protected Entity subtitleEntity;
    protected Map<String, Entity> textures;


    protected SceneLoader(GolemMain engine, String path) {
        this.golemMain = engine;
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
                throw new SceneLoaderException(path, e);
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
        golemMain.setSceneShaders(getSLClass(), _sceneData.shaders);
        if (_sceneData.textures != null) {
            for (TextureData texture : _sceneData.textures) {
                createTexture(texture);
            }
        }
        golemMain.resize();
    }

    private void createBackground(BackgroundData bg){
        if (bg == null) return;
        bgEntity = golemMain.world.createEntity();
        entities.add(bgEntity);
        golemMain.world.addComponent(bgEntity, new TextureComponent(new Texture(Gdx.files.internal(bg.file)), true));
        golemMain.world.addComponent(bgEntity, new PositionComponent(0, 0));
        golemMain.world.addComponent(bgEntity, new SceneIdComponent(getSLClass()));
        if (bg.parallax_speedFactor > 0) {
            golemMain.world.addComponent(bgEntity, new ParallaxComponent(bg.parallax_speedFactor, bg.amplitude));
        }
    }

    private void createSubtitle(SubtitleData subtitle) {
        if (subtitle == null) return;
        if (subtitle.initDelay > 0) {
            golemMain.world.addComponent(golemMain.world.createEntity(), new TimerComponent(subtitle.initDelay,
                ()->loadDialog(subtitle)));
        } else {
            loadDialog(subtitle);
        }
    }

    private void loadDialog(SubtitleData subtitle) {
        subtitleEntity = golemMain.world.createEntity();
        golemMain.world.addComponent(subtitleEntity, new SceneIdComponent(getSLClass()));

        if (subtitle.text.length == 1) {
            golemMain.world.addComponent(subtitleEntity, new SubtitleComponent(subtitle.text[0], getColor(subtitle.textColor), null));
        } else {
            SubtitleMultilineComponent s = new SubtitleMultilineComponent(Arrays.asList(subtitle.text), getColor(subtitle.textColor), null);
            s.maxLines = subtitle.maxLines;
            s.scrollSpeed = subtitle.scrollSpeed;
            s.endDuration = subtitle.endDuration;
            golemMain.world.addComponent(subtitleEntity, s);
        }
    }

    private void createTexture(TextureData textureData) {
        Entity textureEntity = golemMain.world.createEntity();
        textures.put(textureData.id, textureEntity);
        TextureComponent tc = new TextureComponent(new Texture(Gdx.files.internal(textureData.path)), false);
        golemMain.world.addComponent(textureEntity, new SceneIdComponent(getSLClass()));
        golemMain.world.addComponent(textureEntity, tc);
        golemMain.world.addComponent(textureEntity, new PositionComponent(textureData.posX, textureData.posY));
        golemMain.world.addComponent(textureEntity, new SizeComponent(textureData.width, textureData.height));
        if (textureData.shader != null) {
            ShaderType type = ShaderType.fromShaderName(textureData.shader);
            if (type != ShaderType.NONE) {
                Entity entity  = golemMain.world.getEntityWith(type.shaderComponent).orElseThrow(()->new InvalidShaderName(textureData.shader));
                tc.shader = golemMain.world.getComponent(entity, ShaderEffectComponent.class);
                tc.shaderEntity = entity;
            }
        }
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
            golemMain.world.deleteEntity(entity);
        }
    }

    private void disposeTexture(Entity entity) {
        TextureComponent t = golemMain.world.getComponent(entity, TextureComponent.class);
        if (t != null) {
            t.texture.dispose();
            t.texture = null;
        }
    }

    public Entity createDialog(String text, Vector2 position, Runnable onClick, BubbleStyle.BubbleTail bStyle) {
        Entity entity = golemMain.world.createEntity();
        golemMain.world.addComponent(entity, new SceneIdComponent(getSLClass()));
        DialogComponent dc = new DialogComponent(text);
        dc.style.tail = bStyle;
        golemMain.world.addComponent(entity, dc);
        Texture t = golemMain.bubbleTextureGenerator.generateBubble(text, dc.style, dc.size);
        golemMain.world.addComponent(entity, new PositionComponent(position));
        golemMain.world.addComponent(entity, new TextureComponent(t, false));
        golemMain.world.addComponent(entity, new InteractiveComponent( ()->{
            if (onClick != null) onClick.run();
            golemMain.world.addComponent(golemMain.world.createEntity(), new TimerComponent(3f, () ->{
                golemMain.world.deleteEntity(entity);
                t.dispose();
            }));
        }, t.getWidth(), t.getHeight()));
        return entity;
    }

    protected void createNextLoaderAction(Entity entity, SceneLoader nextScene) {
        SizeComponent s = golemMain.world.getComponent(entity, SizeComponent.class);
        golemMain.world.addComponent(entity, new InteractiveComponent( ()->{
            if (golemMain.filterSceneCurrent.filteringClass == getSLClass()) {
                golemMain.loadScene(nextScene, 1.5f);
            }
        }, s.width(), s.height()));
    }

    protected void createTimeAfterSubtitlesForNextScene(float subtitleDelay, SceneLoader nextScene) {
        Entity timer = golemMain.world.createEntity();
        golemMain.world.addComponent(timer, new TimerComponent(subtitleDelay,  ()-> {
            SubtitleMultilineComponent c = golemMain.world.getComponent(subtitleEntity, SubtitleMultilineComponent.class);
            if (c != null) c.onComplete = () -> {
                if (golemMain.filterSceneCurrent.filteringClass == getSLClass()) {
                    golemMain.loadScene(nextScene, 1.5f);
                }
            };
        }));
        golemMain.world.addComponent(timer, new SceneIdComponent(getSLClass()));
        entities.add(timer);
    }

    protected void setDefaultActionForLamp(String lampName) {
        Entity lamp = java.util.Objects.requireNonNull(textures.get(lampName), "lamp "+lampName+" is null");
        TextureComponent tc = golemMain.world.getComponent(lamp, TextureComponent.class);
        golemMain.world.addComponent(lamp, new InteractiveComponent(tc::swapShaders,
            tc.texture.getWidth(), tc.texture.getHeight()));
    }

/*

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
