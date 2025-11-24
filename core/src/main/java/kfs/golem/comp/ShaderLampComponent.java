package kfs.golem.comp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.ecs.KfsWorld;

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
    public void setUniforms(Entity entity, ShaderEffectComponent pec) {
        TimeComponent tc = world.getComponent(entity, TimeComponent.class);
        pec.shader.setUniformf("u_time", tc.time);
        pec.shader.setUniformf("u_intensity", intensity);
        pec.shader.setUniformf("u_speed", speed);
        pec.shader.setUniformf("u_noiseScale", noiseScale);
        pec.shader.setUniformf("u_colorTint", tint.r, tint.g, tint.b);
    }

    public static Entity register(KfsWorld world) {
        Entity e = world.createEntity();
        ShaderLampComponent lamp = new ShaderLampComponent(world);
        world.addComponent(e, lamp);
        world.addComponent(e, new TimeComponent());
        world.addComponent(e, new ShaderEffectComponent("shaders/lamp.vert", "shaders/lamp.frag",
            new Texture(Gdx.files.internal("textures/lamp.png")), lamp));
        return e;
    }
}
