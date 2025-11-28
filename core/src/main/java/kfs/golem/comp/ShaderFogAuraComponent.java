package kfs.golem.comp;

import com.badlogic.gdx.math.Vector2;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.ecs.KfsWorld;

import java.util.Map;

public class ShaderFogAuraComponent implements KfsComp, PostEffectComponent.PecParams {

    private final KfsWorld world;

    public ShaderFogAuraComponent(KfsWorld world) {
        this.world = world;
    }

    @Override
    public void setUniforms(Entity entity, Map<String, Float> params, PostEffectComponent pec, Vector2 wSize) {
        TimeComponent tc = world.getComponent(entity, TimeComponent.class);
        pec.shader.setUniformf("u_time", tc.time);
        pec.shader.setUniformf("u_resolution", wSize);
        pec.shader.setUniformf("u_ceilingY",
            params.getOrDefault("ceiling",.7f));
        pec.shader.setUniformf("u_intensity", params.getOrDefault("intensity", 0.7f));
        pec.shader.setUniformf("u_color",
            params.getOrDefault("colorR", 0.2f),
            params.getOrDefault("colorG", 0.2f),
            params.getOrDefault("colorB", 0.5f));
        pec.shader.setUniformf("u_speed", params.getOrDefault("speed",1.0f));
        pec.shader.setUniformf("u_detail", params.getOrDefault("detail",2.0f));
        pec.shader.setUniformf("u_falloff", params.getOrDefault("falloff",.4f));


    }

    public static Entity register(KfsWorld world) {
        Entity e = world.createEntity();
        ShaderFogAuraComponent fog = new ShaderFogAuraComponent(world);
        world.addComponent(e, fog);
        world.addComponent(e, new TimeComponent());
        world.addComponent(e, new PostEffectComponent("shaders/fog.vert", "shaders/fog_aura.frag", fog));
        return e;
    }
}
