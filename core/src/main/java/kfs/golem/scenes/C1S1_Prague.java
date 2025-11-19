package kfs.golem.scenes;

import com.badlogic.gdx.graphics.Color;
import kfs.golem.GolemMain;
import kfs.golem.comp.SceneIdComponent;
import kfs.golem.comp.ShaderComponent;
import kfs.golem.comp.TimerComponent;
import kfs.golem.ecs.Entity;

import java.util.ArrayList;
import java.util.List;

public class C1S1_Prague implements SceneLoader {

    private List<Entity> entities = new ArrayList<>();

    @Override
    public void load(GolemMain engine) {
        Entity bgEntity = engine.createLayer("images/prague_mid.png", 0.3f);
        engine.world.addComponent(bgEntity, new SceneIdComponent(getClass()));
        engine.world.addComponent(engine.world.createEntity(), new TimerComponent(3f, ()->{
            Entity subtitle = engine.createSubtitle("Město se probouzí. Dýchá těžce, jako starý člověk, který nespal klidně.",
                Color.BLACK, ()->engine.loadScene(new C1S2_Laboratory()));
            engine.world.addComponent(subtitle, new SceneIdComponent(getClass()));
            entities.add(subtitle);
        }));
        engine.world.addComponent(bgEntity, new ShaderComponent(GolemMain.ShaderType.FOG_SEPIA_SLIDE, bgEntity));
        entities.add(bgEntity);

    }

    @Override
    public void unload(GolemMain engine) {
        if (entities != null) {
            for (Entity entity : entities) {
                engine.world.deleteEntity(entity);
            }
        }
    }

    @Override
    public Class<? extends SceneLoader> getSLClass() {
        return getClass();
    }
}
