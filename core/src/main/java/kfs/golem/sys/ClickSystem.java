package kfs.golem.sys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import kfs.golem.GolemMain;
import kfs.golem.comp.ClickComponent;
import kfs.golem.ecs.KfsSystem;

public class ClickSystem implements KfsSystem {

    private final GolemMain golemMain;

    public ClickSystem(GolemMain golemMain) {
        this.golemMain = golemMain;
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isTouched()) {
            Vector2 click = new Vector2(Gdx.input.getX(), (Gdx.graphics.getHeight() - Gdx.input.getY()));
            Gdx.app.log("ClickSystem", "Click "+click);
            golemMain.world.addComponent(golemMain.world.createEntity(), new ClickComponent(click));
        }
    }
}
