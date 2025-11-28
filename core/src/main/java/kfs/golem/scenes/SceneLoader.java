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
import kfs.golem.sys.RenderSystem;
import kfs.golem.utils.BubbleStyle;
import kfs.golem.utils.InvalidShaderName;
import kfs.golem.utils.SceneLoaderException;

import java.util.*;

public abstract class SceneLoader {

    private static final int SUBTITLE_Z_ORDER = 880;
    private static final int DIALOG_Z_ORDER = 880;

    protected final GolemMain golemMain;
    protected final List<Entity> entities = new ArrayList<>();
    protected final SceneData _sceneData;

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
        if (_sceneData.shaders != null)
            for (FullScreenShaderData sd : _sceneData.shaders)
                createFSShaders(sd);
        if (_sceneData.textures != null) {
            for (TextureData texture : _sceneData.textures) {
                createTexture(texture);
            }
        }
        createSubtitle(_sceneData.subtitle);
    }

    private void createBackground(BackgroundData bg){
        if (bg == null) return;
        Entity bgEntity = golemMain.world.createEntity();
        entities.add(bgEntity);
        golemMain.world.addComponent(bgEntity, new TextureComponent(new Texture(Gdx.files.internal(bg.file))));
        golemMain.world.addComponent(bgEntity, new PositionComponent(0, 0));
        golemMain.world.addComponent(bgEntity, new RenderComponent(getSLClass(), bg.z, (RenderSystem rs)->
            rs.renderTexturesFullScreen(bgEntity)
        ));
        if (bg.parallax_speedFactor > 0) {
            golemMain.world.addComponent(bgEntity, new ParallaxComponent(bg.parallax_speedFactor, bg.amplitude));
        }
    }

    private void createFSShaders(FullScreenShaderData sd) {
        Entity e = golemMain.world.createEntity();
        golemMain.world.addComponent(e, new RenderComponent(getSLClass(), sd.z, (RenderSystem rs)->
            rs.renderFsEffect(ShaderType.fromShaderName(sd.name), Optional.ofNullable(sd.params).orElse(new HashMap<>()))
        ));
    }

    private void createSubtitle(SubtitleData subtitle) {
        if (subtitle == null) return;
        if (subtitle.initDelay > 0) {
            golemMain.world.addComponent(golemMain.world.createEntity(), new TimerComponent(subtitle.initDelay,
                ()-> loadSubtitle(subtitle)));
        } else {
            loadSubtitle(subtitle);
        }
    }

    private void loadSubtitle(SubtitleData subtitle) {
        subtitleEntity = golemMain.world.createEntity();

        if (subtitle.text.length == 1) {
            golemMain.world.addComponent(subtitleEntity, new SubtitleComponent(subtitle.text[0], getColor(subtitle.textColor), null));
            golemMain.world.addComponent(subtitleEntity, new RenderComponent(getSLClass(), SUBTITLE_Z_ORDER, rs->
                rs.renderSubtitles(subtitleEntity)
            ));
        } else {
            SubtitleMultilineComponent s = new SubtitleMultilineComponent(Arrays.asList(subtitle.text), getColor(subtitle.textColor), null);
            s.maxLines = subtitle.maxLines;
            s.scrollSpeed = subtitle.scrollSpeed;
            s.endDuration = subtitle.endDuration;
            golemMain.world.addComponent(subtitleEntity, s);
            golemMain.world.addComponent(subtitleEntity, new RenderComponent(getSLClass(), SUBTITLE_Z_ORDER, rs->
                rs.renderMultilineSubtitles(subtitleEntity)
            ));
        }
    }

    private void createTexture(TextureData textureData) {
        Entity textureEntity = golemMain.world.createEntity();
        textures.put(textureData.id, textureEntity);
        TextureComponent tc = new TextureComponent(
            new Texture(Gdx.files.internal(textureData.path)));
        if (textureData.shader != null) {
            createShader(tc, textureData.shader);
        }
        golemMain.world.addComponent(textureEntity, tc);
        golemMain.world.addComponent(textureEntity, new PositionComponent(textureData.posX, textureData.posY));
        golemMain.world.addComponent(textureEntity, new SizeComponent(textureData.width, textureData.height));
        golemMain.world.addComponent(textureEntity, new RenderComponent(getSLClass(), textureData.z, rs->
            rs.renderTextures(textureEntity)
        ));
    }

    private void createShader(TextureComponent tc, ShaderData sd) {
        ShaderType type = ShaderType.fromShaderName(sd.shader);
        if (type != ShaderType.NONE) {
            Texture texture = null;
            if (!(sd.shaderTexture == null || sd.shaderTexture.length() <= 0)) {
                texture = new Texture(Gdx.files.internal(sd.shaderTexture));
            }
            Map<String, Float> params = sd.shaderParameters==null?new HashMap<>():sd.shaderParameters;
            Entity entity  = golemMain.world.getEntityWith(type.shaderComponent).orElseThrow(()->new InvalidShaderName(sd.shader));
            ShaderEffectComponent sec = golemMain.world.getComponent(entity, ShaderEffectComponent.class);
            tc.setShader(sec, entity, params, texture, type);
        }
    }

    public abstract Class<? extends SceneLoader> getSLClass();

    public void unload() {
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
            if (t.shader != null) {
                t.shader.enabled = false;
                if (t.shader.shaderTexture != null) {
                    t.shader.shaderTexture.dispose();
                }
            }
        }
    }

    protected Entity createDialog(String text, Vector2 position, Runnable onClick, BubbleStyle.BubbleTail bStyle) {
        Entity entity = golemMain.world.createEntity();

        DialogComponent dc = new DialogComponent(text, new BubbleStyle(bStyle));
        golemMain.world.addComponent(entity, dc);
        Texture t = golemMain.bubbleTextureGenerator.generateBubble(text, dc.style, dc.size);

        golemMain.world.addComponent(entity, new PositionComponent(position));
        golemMain.world.addComponent(entity, new TextureComponent(t));
        golemMain.world.addComponent(entity, new InteractiveComponent( ()->{
            if (onClick != null) onClick.run();
            golemMain.world.addComponent(golemMain.world.createEntity(), new TimerComponent(3f, () ->{
                golemMain.world.deleteEntity(entity);
                t.dispose();
            }));
        }, t.getWidth(), t.getHeight()));
        golemMain.world.addComponent(entity, new RenderComponent(getSLClass(), DIALOG_Z_ORDER, rs->{
            rs.renderTextures(entity);
            rs.renderDialog(entity);
        }));

        return entity;
    }

    protected void createNextLoaderAction(Entity entity, SceneLoader nextScene) {
        SizeComponent s = golemMain.world.getComponent(entity, SizeComponent.class);
        golemMain.world.addComponent(entity, new InteractiveComponent( ()->{
            if (golemMain.sceneCurrent.getSLClass() == getSLClass()) {
                golemMain.loadScene(nextScene, 1.5f);
            }
        }, s.size.x, s.size.y));
    }

    protected void createTimeAfterSubtitlesForNextScene(float subtitleDelay, SceneLoader nextScene) {
        Entity timer = golemMain.world.createEntity();
        golemMain.world.addComponent(timer, new TimerComponent(subtitleDelay,  ()-> {
            SubtitleMultilineComponent c = golemMain.world.getComponent(subtitleEntity, SubtitleMultilineComponent.class);
            if (c != null) c.onComplete = () -> {
                if (golemMain.sceneCurrent.getSLClass() == getSLClass()) {
                    golemMain.loadScene(nextScene, 1.5f);
                }
            };
        }));
        entities.add(timer);
    }

    protected void setDefaultActionForLamp(String lampName) {
        Entity lamp = java.util.Objects.requireNonNull(textures.get(lampName), "lamp "+lampName+" is null");
        TextureComponent tc = golemMain.world.getComponent(lamp, TextureComponent.class);

        golemMain.world.addComponent(lamp, new InteractiveComponent(tc::turn,
            tc.texture.getWidth(), tc.texture.getHeight()));
    }

}
