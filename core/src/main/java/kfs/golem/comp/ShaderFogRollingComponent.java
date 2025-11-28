package kfs.golem.comp;

import com.badlogic.gdx.math.Vector2;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.ecs.KfsWorld;

import java.util.Map;

public class ShaderFogRollingComponent implements KfsComp, PostEffectComponent.PecParams {

    private final KfsWorld world;

    public ShaderFogRollingComponent(KfsWorld world) {
        this.world = world;
    }

    @Override
    public void setUniforms(Entity entity, Map<String, Float> params, PostEffectComponent pec, Vector2 wSize) {
        TimeComponent tc = world.getComponent(entity, TimeComponent.class);
        pec.shader.setUniformf("u_time", tc.time);
        pec.shader.setUniformf("u_intensity",
            params.getOrDefault("intensity", 0.7f));
        pec.shader.setUniformf("u_scroll",
            params.getOrDefault("scrollX", 7f),
            params.getOrDefault("scrollY", 0.1f));
        pec.shader.setUniformf("u_fogColor",
            params.getOrDefault("fogColorR", 0.5f),
            params.getOrDefault("fogColorG", 0.5f),
            params.getOrDefault("fogColorB", 0.5f));
        pec.shader.setUniformf("u_noiseScale",
            params.getOrDefault("noiseScale",1.2f));
        pec.shader.setUniformf("u_heightFalloff",
            params.getOrDefault("heightFalloff", 1.2f));
        pec.shader.setUniformf("u_bandWidth",
            params.getOrDefault("bandWidth", .82f));


    }

    public static Entity register(KfsWorld world) {
        Entity e = world.createEntity();
        ShaderFogRollingComponent fog = new ShaderFogRollingComponent(world);
        world.addComponent(e, fog);
        world.addComponent(e, new TimeComponent());
        world.addComponent(e, new PostEffectComponent("shaders/fog.vert", "shaders/fog_rolling.frag", fog));
        return e;
    }
}
