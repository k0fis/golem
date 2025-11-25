package kfs.golem.comp;

import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.ecs.KfsWorld;

public class ShaderFogRollingComponent implements KfsComp, PostEffectComponent.PecParams {

    private final KfsWorld world;

    public ShaderFogRollingComponent(KfsWorld world) {
        this.world = world;
    }

    @Override
    public void setUniforms(Entity entity, PostEffectComponent pec, float delta) {
        TimeComponent tc = world.getComponent(entity, TimeComponent.class);
        pec.shader.setUniformf("u_time", tc.time);
        pec.shader.setUniformf("u_intensity", 0.7f);
        pec.shader.setUniformf("u_scroll", 7f, 0.1f);
        pec.shader.setUniformf("u_fogColor", 0.5f, 0.5f, 0.5f);
        pec.shader.setUniformf("u_noiseScale", 1.2f);
        pec.shader.setUniformf("u_heightFalloff", 1.2f);
        pec.shader.setUniformf("u_bandWidth", .82f);


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
