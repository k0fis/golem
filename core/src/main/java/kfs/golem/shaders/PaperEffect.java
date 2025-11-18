package kfs.golem.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import kfs.golem.GolemMain;
import kfs.golem.comp.ShaderComponent;
import kfs.golem.comp.TextureComponent;
import kfs.golem.ecs.Entity;

import static kfs.golem.GolemMain.ShaderType.PAPER;

public class PaperEffect extends ShaderEffect {

    private final GolemMain golem;

    public PaperEffect(GolemMain golem) {
        super(golem.world, PAPER,"shaders/paper.vert", "shaders/paper.frag");
        this.golem = golem;
    }

    @Override
    protected void setUniforms(Entity e, ShaderComponent sc, float delta) {
        sc.time += delta;

        shader.setUniformf("u_time", sc.time);
        shader.setUniformi("u_texture", 0);
        TextureComponent tex = golem.world.getComponent(e, TextureComponent.class);
        Vector2 wSize = golem.getWorldSize();
        float imgW = tex.texture.getRegionWidth();
        float imgH = tex.texture.getRegionHeight();
        float scale = Math.min(wSize.x / imgW, wSize.y / imgH);
        shader.setUniformf("u_resolution", imgW * scale, imgH * scale);
    }
}
