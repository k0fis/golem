package kfs.golem.comp;

import com.badlogic.gdx.math.Vector2;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.ecs.KfsWorld;

import java.util.Map;

public class ShaderFogSepiaSlideComponent implements KfsComp, PostEffectComponent.PecParams {

    private final KfsWorld world;

    public ShaderFogSepiaSlideComponent(KfsWorld world) {
        this.world = world;
    }

    @Override
    public void setUniforms(Entity entity, Map<String, Float> params, PostEffectComponent pec, Vector2 wSize) {
        TimeComponent tc = world.getComponent(entity, TimeComponent.class);
        pec.shader.setUniformf("u_time", tc.time);
    }

    public static Entity register(KfsWorld world) {
        Entity fogEntity = world.createEntity();
        ShaderFogSepiaSlideComponent sepia = new ShaderFogSepiaSlideComponent(world);
        world.addComponent(fogEntity, sepia);
        world.addComponent(fogEntity, new TimeComponent());
        world.addComponent(fogEntity, new PostEffectComponent("shaders/fog.vert", "shaders/fog_sepia_slide.frag", sepia));
        return fogEntity;
    }
}
