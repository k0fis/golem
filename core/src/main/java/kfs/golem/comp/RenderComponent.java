package kfs.golem.comp;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import kfs.golem.ecs.KfsComp;
import kfs.golem.scenes.SceneLoader;
import kfs.golem.sys.RenderSystem;

public class RenderComponent implements KfsComp {

    public final Class<? extends SceneLoader> sceneIdClass;
    public final int zOrder;
    public final Render onRender;

    public RenderComponent(Class<? extends SceneLoader> sceneIdClass, int zOrder, Render onRender) {
        this.sceneIdClass = sceneIdClass;
        this.zOrder = zOrder;
        this.onRender = onRender;
    }

    public interface Render {
        void render(RenderSystem rs);
    }
}
