package kfs.golem.comp;

import com.badlogic.gdx.Gdx;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.ecs.KfsWorld;

public class ShaderFogComponent implements KfsComp, PostEffectComponent.PecParams {

    private final KfsWorld world;

    public ShaderFogComponent(KfsWorld world) {
        this.world = world;
    }

    @Override
    public void setUniforms(Entity entity, PostEffectComponent pec, float delta) {
        TimeComponent tc = world.getComponent(entity, TimeComponent.class);
        Gdx.app.log("ShaderFogComponent", "setUniforms " + tc.time);
        pec.shader.setUniformf("u_time", tc.time);
    }

    public static Entity register(KfsWorld world) {
        Entity fogEntity = world.createEntity();
        ShaderFogComponent sfc = new ShaderFogComponent(world);
        world.addComponent(fogEntity, sfc);
        world.addComponent(fogEntity, new TimeComponent());
        world.addComponent(fogEntity, new PostEffectComponent("shaders/fog.vert", "shaders/fog.frag", sfc));
        return fogEntity;
    }
}
