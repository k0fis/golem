package kfs.golem.comp;

import com.badlogic.gdx.math.Vector2;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.ecs.KfsWorld;

import java.util.Map;

public class ShaderOutlineComponent implements KfsComp, ShaderEffectComponent.PecParams {

    private final KfsWorld world;
    private float dotSize = 0.05f;
    private float speed = 10f;
    private float dotColorR = 0.2f;
    private float dotColorG = 0.6f;
    private float dotColorB = 1.f;
    private float centerX = 0.5f;
    private float centerY = 0.5f;

    public ShaderOutlineComponent(KfsWorld world) {
        this.world = world;
    }

    @Override
    public void setUniforms(Entity entity, Map<String, Float> params, ShaderEffectComponent pec, Vector2 size) {
        TimeComponent tc = world.getComponent(entity, TimeComponent.class);
        pec.shader.setUniformf("u_time",
            tc.time);
        pec.shader.setUniformf("u_speed",
            params.getOrDefault("speed", speed));
        pec.shader.setUniformf("u_dotSize",
            params.getOrDefault("dotSize", dotSize));
        pec.shader.setUniformf("u_dotColor",
            params.getOrDefault("dotColorR", dotColorR),
            params.getOrDefault("dotColorG", dotColorG),
            params.getOrDefault("dotColorB", dotColorB));
        pec.shader.setUniformf("u_center",
            params.getOrDefault("centerX", centerX),
            params.getOrDefault("centerY", centerY));

        //pec.shader.setUniformf("u_colorTint", 0.2f, 0.6f, 1f);  // jemná azurová
    }

    public static Entity register(KfsWorld world) {
        Entity e = world.createEntity();
        ShaderOutlineComponent outline = new ShaderOutlineComponent(world);
        world.addComponent(e, outline);
        world.addComponent(e, new TimeComponent());
        world.addComponent(e, new ShaderEffectComponent("shaders/outline.vert", "shaders/outline.frag", outline));
        return e;
    }
}
