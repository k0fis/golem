package kfs.golem.comp;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.ecs.KfsWorld;

import java.util.Map;

public class ShaderOutline2Component implements KfsComp, ShaderEffectComponent.PecParams {

    private final KfsWorld world;
    private Vector3 outlineColor = new Vector3(0.2f, 0.6f, 1f);
    private float pulseDuration = 0.5f;
    private float pulseInterval = 2.4f;

    public ShaderOutline2Component(KfsWorld world) {
        this.world = world;
    }

    @Override
    public void setUniforms(Entity entity, Map<String, Float> params, ShaderEffectComponent pec, Vector2 size) {
        TimeComponent tc = world.getComponent(entity, TimeComponent.class);
        pec.shader.setUniformf("u_time", tc.time);
        pec.shader.setUniformf("u_outlineColor",
            params.getOrDefault("outlineColorR", outlineColor.x),
            params.getOrDefault("outlineColorG", outlineColor.y),
            params.getOrDefault("outlineColorB", outlineColor.z));
        pec.shader.setUniformf("u_pulseDuration",
            params.getOrDefault("pulseDuration", pulseDuration));
        pec.shader.setUniformf("u_pulseInterval",
            params.getOrDefault("pulseInterval", pulseInterval));
    }

    public static Entity register(KfsWorld world) {
        Entity e = world.createEntity();
        ShaderOutline2Component outline = new ShaderOutline2Component(world);
        world.addComponent(e, outline);
        world.addComponent(e, new TimeComponent());
        world.addComponent(e, new ShaderEffectComponent("shaders/outline.vert", "shaders/outline2.frag", outline));
        return e;
    }
}
