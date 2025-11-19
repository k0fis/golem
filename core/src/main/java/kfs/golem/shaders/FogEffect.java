package kfs.golem.shaders;

import com.badlogic.gdx.Gdx;
import kfs.golem.GolemMain;
import kfs.golem.comp.ShaderComponent;
import kfs.golem.ecs.Entity;

import static kfs.golem.GolemMain.ShaderType.FOG;

public class FogEffect extends ShaderEffect {

    public FogEffect(GolemMain golem) {
        super(golem, FOG,"shaders/fog.vert", "shaders/fog.frag");
    }

    @Override
    protected void setUniforms(Entity e, ShaderComponent sc, float delta) {
        setTime(e, sc);
        setResolution(e, sc);
        float introTime =  sc.time / 3f;
        shader.setUniformf("u_introProgress", introTime);
        //Gdx.app.error("FogEffect", "introTime: " + introTime);
    }
}
