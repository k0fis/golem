package kfs.golem.comp;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.ecs.KfsWorld;

import java.util.Map;

public class ShaderSymbolComponent implements KfsComp, ShaderEffectComponent.PecParams {

    private final KfsWorld world;

    public ShaderSymbolComponent(KfsWorld world) {
        this.world = world;
    }

    @Override
    public void setUniforms(Entity entity, Map<String, Float> params, ShaderEffectComponent pec, Vector2 size) {
        TimeComponent tc = world.getComponent(entity, TimeComponent.class);
        pec.shader.setUniformf("u_time", tc.time);
        pec.shader.setUniformf("u_fade", params.getOrDefault("fade", 1f));
        pec.shader.setUniformf("u_duration", params.getOrDefault("duration", 5f));
        pec.shader.setUniformf("u_fillProgress", Math.min(1f, tc.time/params.getOrDefault("duration", 5f)));
    }

    public static Entity register(KfsWorld world) {
        Entity e = world.createEntity();
        ShaderSymbolComponent symComp = new ShaderSymbolComponent(world);
        world.addComponent(e, symComp);
        world.addComponent(e, new TimeComponent());
        world.addComponent(e, new ShaderEffectComponent("shaders/symbol.vert","shaders/symbol.glsl", symComp));
        return e;
    }
}
