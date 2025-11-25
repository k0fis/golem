package kfs.golem.comp;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.ecs.KfsWorld;

public class ShaderOutlineComponent implements KfsComp, ShaderEffectComponent.PecParams {

    private final KfsWorld world;

    public ShaderOutlineComponent(KfsWorld world) {
        this.world = world;
    }

    @Override
    public void setUniforms(Entity entity, ShaderEffectComponent pec) {
        TimeComponent tc = world.getComponent(entity, TimeComponent.class);
        pec.shader.setUniformf("u_speed", 0.25f);               // pomaličké obíhání
        pec.shader.setUniformf("u_width", 0.015f);              // úzký pulz
        pec.shader.setUniformf("u_glowColor", 0.2f, 0.6f, 1f);  // jemná azurová
        pec.shader.setUniformf("u_time", tc.time);
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
