package kfs.golem.comp;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
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
}
