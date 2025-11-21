package kfs.golem.shaders;

import kfs.golem.GolemMain;
import kfs.golem.comp.ShaderComponent;
import kfs.golem.comp.ShaderLocalComponent;
import kfs.golem.ecs.Entity;

public class LampEffect extends ShaderEffect {

    public LampEffect(GolemMain golemMain) {
        super(golemMain, "shaders/lamp.vert", "shaders/lamp.frag");
    }

    @Override
    public void setUniforms(Entity e, ShaderLocalComponent slc, float delta) {
        shader.setUniformf("u_time", slc.time);
        shader.setUniformf("u_intensity", 0.35f);
        shader.setUniformf("u_speed", 1.8f);
        shader.setUniformf("u_noiseScale", 6.0f);
        shader.setUniformf("u_colorTint", 1.08f, 0.98f, 0.85f);
    }
}
