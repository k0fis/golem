package kfs.golem.shaders;

import kfs.golem.GolemMain;
import kfs.golem.comp.ShaderComponent;
import kfs.golem.ecs.Entity;

public class Fog2Effect extends ShaderEffect {

    public Fog2Effect(GolemMain golem) {
        super(golem,"shaders/fog.vert", "shaders/fog2.frag");
    }

    @Override
    protected void setUniforms(Entity e, ShaderComponent sc, float delta) {
        setTime(e, sc);
        setResolution(e, sc);
        float introTime =  sc.time / 3f;
        shader.setUniformf("u_introProgress", introTime);
    }
}
