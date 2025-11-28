package kfs.golem.comp;

import com.badlogic.gdx.math.Vector2;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.ecs.KfsWorld;

import java.util.Map;

public class ShaderFogComponent implements KfsComp, PostEffectComponent.PecParams {

    private final KfsWorld world;

    public ShaderFogComponent(KfsWorld world) {
        this.world = world;
    }

    @Override
    public void setUniforms(Entity entity, Map<String, Float> params, PostEffectComponent pec, Vector2 wSize) {
        TimeComponent tc = world.getComponent(entity, TimeComponent.class);
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
