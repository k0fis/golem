package kfs.golem.scenes;

import com.badlogic.gdx.graphics.Color;
import kfs.golem.GolemMain;
import kfs.golem.comp.SceneIdComponent;
import kfs.golem.comp.ShaderComponent;
import kfs.golem.comp.TimerComponent;
import kfs.golem.ecs.Entity;

import java.util.ArrayList;
import java.util.List;

public class C1S2_Laboratory implements SceneLoader {

    private final List<Entity> entities = new ArrayList<>();

    @Override
    public void load(GolemMain engine) {
        Entity bgEntity = engine.createLayer("images/prague_bridge.png", 0);
        engine.world.addComponent(bgEntity, new SceneIdComponent(getClass()));
        Entity subtitle = engine.createSubtitle("Karlův Most", Color.BLACK, null);
        engine.world.addComponent(subtitle, new SceneIdComponent(getClass()));
        //engine.world.addComponent(engine.world.createEntity(), new TimerComponent(1f, () -> {
            Entity shaderEntity = engine.world.createEntity();
            engine.world.addComponent(shaderEntity, new SceneIdComponent(getClass()));
            engine.world.addComponent(shaderEntity, new ShaderComponent(GolemMain.ShaderType.FOG, bgEntity));
            entities.add(shaderEntity);
        //}));

        entities.addAll(List.of(bgEntity, subtitle));
    }

    @Override
    public void unload(GolemMain engine) {
        for (Entity entity : entities) {
            engine.world.deleteEntity(entity);
        }
    }


    @Override
    public Class<? extends SceneLoader> getSLClass() {
        return getClass();
    }
}
