package kfs.golem.comp;

import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.ecs.KfsWorld;

public class ShaderFog2Component implements KfsComp, PostEffectComponent.PecParams {

    private final KfsWorld world;

    public ShaderFog2Component(KfsWorld world) {
        this.world = world;
    }

    @Override
    public void setUniforms(Entity entity, PostEffectComponent pec, float delta) {
        TimeComponent tc = world.getComponent(entity, TimeComponent.class);
        pec.shader.setUniformf("u_time", tc.time);
    }

    public static Entity register(KfsWorld world) {
        Entity fogEntity = world.createEntity();
        ShaderFog2Component sf2c = new ShaderFog2Component(world);
        world.addComponent(fogEntity, sf2c);
        world.addComponent(fogEntity, new TimeComponent());
        world.addComponent(fogEntity, new PostEffectComponent("shaders/fog.vert", "shaders/fog2.frag", sf2c));
        return fogEntity;
    }
}
