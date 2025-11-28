package kfs.golem.comp;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.ecs.KfsWorld;

import java.util.Map;

public class ShaderLampComponent implements KfsComp, ShaderEffectComponent.PecParams {

    private final KfsWorld world;

    public float intensity = 0.35f;
    public float speed = 1.8f;
    public float noiseScale = 6f;
    public Color tint = new Color(1.08f, 0.98f, 0.85f, 1.0f);

    public ShaderLampComponent(KfsWorld world) {
        this.world = world;
    }

    @Override
    public void setUniforms(Entity entity, Map<String, Float> params, ShaderEffectComponent pec, Vector2 size) {
        TimeComponent tc = world.getComponent(entity, TimeComponent.class);
        pec.shader.setUniformf("u_time", tc.time);
        pec.shader.setUniformf("u_intensity", params.getOrDefault("intensity", intensity));
        pec.shader.setUniformf("u_speed", params.getOrDefault("speed", speed));
        pec.shader.setUniformf("u_noiseScale", params.getOrDefault("noiseScale", noiseScale));
        pec.shader.setUniformf("u_colorTint",
            params.getOrDefault("tintR", tint.r),
            params.getOrDefault("tintG", tint.g),
            params.getOrDefault("tintB", tint.b));
    }

    public static Entity register(KfsWorld world) {
        Entity e = world.createEntity();
        ShaderLampComponent lamp = new ShaderLampComponent(world);
        world.addComponent(e, lamp);
        world.addComponent(e, new TimeComponent());
        world.addComponent(e, new ShaderEffectComponent("shaders/lamp.vert", "shaders/lamp.frag", lamp));
        return e;
    }
}
