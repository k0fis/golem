package kfs.golem.sys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import kfs.golem.comp.CrossfadeComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsSystem;
import kfs.golem.ecs.KfsWorld;
import kfs.golem.utils.ShaderEffectCompileException;


public class CrossfadeSystem implements KfsSystem {

    private final KfsWorld world;
    private final ShaderProgram shader;
    private final Mesh fullscreenQuad;

    public CrossfadeSystem(KfsWorld world) {
        this.world = world;
        this.shader = new ShaderProgram(Gdx.files.internal("shaders/crossfade.vert"),  Gdx.files.internal("shaders/crossfade.frag"));
        if (!shader.isCompiled()) {
            throw new ShaderEffectCompileException("shaders/crossfade.vert", "shaders/crossfade.frag", shader.getLog());
        }

        // Fullscreen quad (typický čtyřúhelník)
        fullscreenQuad = new Mesh(true, 4, 6,
            new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0"));

        float[] verts = new float[] {
            -1, -1, 0,   0, 0,
            1, -1, 0,   1, 0,
            1,  1, 0,   1, 1,
            -1,  1, 0,   0, 1
        };

        short[] idx = new short[] { 0,1,2,  2,3,0 };

        fullscreenQuad.setVertices(verts);
        fullscreenQuad.setIndices(idx);
    }


    @Override
    public void update(float delta) {
        for (Entity e : world.getEntitiesWith(CrossfadeComponent.class)) {
            CrossfadeComponent cc = world.getComponent(e, CrossfadeComponent.class);
            cc.time += delta;
            float alpha = cc.time / cc.duration;
            cc.fade = Math.min(1f, Math.max(0f, alpha));   // clamp
        }
    }

    public int render(Texture texA, Texture texB) {
        int inx = 0;
        for (Entity e : world.getEntitiesWith(CrossfadeComponent.class)) {
            CrossfadeComponent cc = world.getComponent(e, CrossfadeComponent.class);

            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
            Gdx.gl.glDisable(GL20.GL_CULL_FACE);

            shader.bind();
            shader.setUniformf("u_alpha", cc.fade);

            texA.bind(0);
            shader.setUniformi("u_texA", 0);

            texB.bind(1);
            shader.setUniformi("u_texB", 1);

            fullscreenQuad.render(shader, GL20.GL_TRIANGLES);
            inx++;
        }
        return inx;
    }

    public void dispose() {
        shader.dispose();
    }
}
