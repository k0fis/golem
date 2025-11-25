package kfs.golem.comp;

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
        pec.shader.setUniformf("u_time", tc.time);
        pec.shader.setUniformf("u_thickness", 0.05f);   // pomalé otáčení
        pec.shader.setUniformf("u_dotSize", 0.05f);   // šířka pulzu
        pec.shader.setUniformf("u_dotColor", 0.2f, 0.6f, 1.0f); // azurová nádech


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
