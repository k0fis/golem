package kfs.golem.shaders;

import kfs.golem.GolemMain;
import kfs.golem.comp.ShaderComponent;
import kfs.golem.ecs.Entity;

import static kfs.golem.GolemMain.ShaderType.FOG_SEPIA_SLIDE;

public class FogSepiaSlideEffect extends ShaderEffect {

    public FogSepiaSlideEffect(GolemMain golem) {
        super(golem, FOG_SEPIA_SLIDE,"shaders/fog_sepia_slide.vert", "shaders/fog_sepia_slide.frag");
    }

    @Override
    protected void setUniforms(Entity e, ShaderComponent sc, float delta) {
        setTime(e, sc);
        setResolution(e, sc);
    }
}
