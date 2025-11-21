package kfs.golem.shaders;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import kfs.golem.GolemMain;
import kfs.golem.comp.ShaderComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.EntityFilter;

import java.util.EnumMap;
import java.util.Map;

import static kfs.golem.shaders.ShaderType.*;

public class ShaderPipeline {

    private final GolemMain golemMain;
    private final Map<ShaderType, ShaderEffect> effects;

    public ShaderPipeline(GolemMain golemMain) {
        this.golemMain = golemMain;
        effects = new EnumMap<>(ShaderType.class);
        ShaderProgram.pedantic = false;
        effects.put(FOG, new FogEffect(golemMain));
        effects.put(FOG2, new Fog2Effect(golemMain));
        effects.put(FOG_SEPIA_SLIDE, new FogSepiaSlideEffect(golemMain));
        effects.put(LAMP, new LampEffect(golemMain));

    }

    public void setShader(SpriteBatch batch, EntityFilter filter) {
        for (Entity e : golemMain.world.getEntitiesWith(ShaderComponent.class)) {
            if (filter.filter(e)) {
                ShaderComponent sc = golemMain.world.getComponent(e, ShaderComponent.class);
                ShaderEffect shader = effects.get(sc.type);
                if (shader != null) {
                    shader.shader.bind();
                    batch.setShader(shader.shader);
                }
            }
        }
    }

    public void setUniforms(float delta, EntityFilter filter) {
        for (Entity e : golemMain.world.getEntitiesWith(ShaderComponent.class)) {
            if (filter.filter(e)) {
                ShaderComponent sc = golemMain.world.getComponent(e, ShaderComponent.class);
                ShaderEffect shader = effects.get(sc.type);
                if (shader != null) {
                    sc.time += delta;
                    shader.setUniforms(e, sc, delta);
                }
            }
        }
    }

    public ShaderEffect getEffect(ShaderType type) {
        return effects.get(type);
    }

    public void dispose() {
        for (ShaderEffect e : effects.values()) e.dispose();
    }
}
