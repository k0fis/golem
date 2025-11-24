package kfs.golem.comp;

import com.badlogic.gdx.Gdx;
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
        Gdx.app.log("ShaderFogComponent", "setUniforms " + tc.time);
        pec.shader.setUniformf("u_time", tc.time);
        pec.shader.setUniformf("u_speed", 1.0f);
        pec.shader.setUniformf("u_scale", 8.0f);
        //pec.shader.setUniformf("u_density", 0.7f);
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
