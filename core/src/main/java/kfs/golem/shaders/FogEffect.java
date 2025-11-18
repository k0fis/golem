package kfs.golem.shaders;

import com.badlogic.gdx.math.Vector2;
import kfs.golem.GolemMain;
import kfs.golem.comp.ShaderComponent;
import kfs.golem.comp.TextureComponent;
import kfs.golem.ecs.Entity;

import static kfs.golem.GolemMain.ShaderType.FOG;

public class FogEffect extends ShaderEffect {

    private final GolemMain golem;

    public FogEffect(GolemMain golem) {
        super(golem.world, FOG,"shaders/fog.vert", "shaders/fog.frag");
        this.golem = golem;
    }

    @Override
    protected void setUniforms(Entity e, ShaderComponent sc, float delta) {
        sc.time += delta;

        TextureComponent tex = world.getComponent(e, TextureComponent.class);
        Vector2 wSize = golem.getWorldSize();
        float imgW = tex.texture.getRegionWidth();
        float imgH = tex.texture.getRegionHeight();
        float scale = Math.min(wSize.x / imgW, wSize.y / imgH);

        shader.setUniformf("u_time", sc.time);
        shader.setUniformf("u_resolution", imgW * scale,
            imgH * scale);
    }
}
