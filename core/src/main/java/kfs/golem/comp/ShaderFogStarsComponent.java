package kfs.golem.comp;

import com.badlogic.gdx.math.Vector2;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.ecs.KfsWorld;

import java.util.Map;

public class ShaderFogStarsComponent implements KfsComp, PostEffectComponent.PecParams {

    private final KfsWorld world;

    public ShaderFogStarsComponent(KfsWorld world) {
        this.world = world;
    }

    @Override
    public void setUniforms(Entity entity, Map<String, Float> params, PostEffectComponent pec, Vector2 wSize) {
        TimeComponent tc = world.getComponent(entity, TimeComponent.class);
        pec.shader.setUniformf("u_time", tc.time);
        pec.shader.setUniformf("u_band", params.getOrDefault("band", 0.12f));
        pec.shader.setUniformf("u_intensity", params.getOrDefault("intensity", 0.2f));
        pec.shader.setUniformf("u_density", params.getOrDefault("density",2.0f));
        pec.shader.setUniformf("u_speed", params.getOrDefault("speed",1.2f));
        pec.shader.setUniformf("u_color",
            params.getOrDefault("colorR",.19f),
            params.getOrDefault("colorG",.17f),
            params.getOrDefault("colorB",.3f));
    }

    public static Entity register(KfsWorld world) {
        Entity e = world.createEntity();
        ShaderFogStarsComponent fog = new ShaderFogStarsComponent(world);
        world.addComponent(e, fog);
        world.addComponent(e, new TimeComponent());
        world.addComponent(e, new PostEffectComponent("shaders/fog.vert", "shaders/fog_stars.frag", fog));
        return e;
    }
}
