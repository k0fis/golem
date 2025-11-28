package kfs.golem.comp;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.ecs.KfsWorld;

import java.util.Map;

public class ShaderCandleFlickerComponent implements KfsComp, ShaderEffectComponent.PecParams {
    //
    private final KfsWorld world;

    public ShaderCandleFlickerComponent(KfsWorld world) {
        this.world = world;
    }

    @Override
    public void setUniforms(Entity entity, Map<String, Float> params, ShaderEffectComponent pec, Vector2 size) {
        TimeComponent tc = world.getComponent(entity, TimeComponent.class);
        pec.shader.setUniformf("u_time", tc.time);
    }

    public static Entity register(KfsWorld world) {
        Entity e = world.createEntity();
        ShaderCandleFlickerComponent flickerComponent = new ShaderCandleFlickerComponent(world);
        world.addComponent(e, flickerComponent);
        world.addComponent(e, new TimeComponent());
        world.addComponent(e, new ShaderEffectComponent("shaders/candle_flicker.vert", "shaders/candle_flicker.frag", flickerComponent));
        return e;
    }

}
