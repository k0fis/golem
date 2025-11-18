package kfs.golem.sys;

import com.badlogic.gdx.Gdx;
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
            golemMain.world.addComponent(golemMain.world.createEntity(),
                new ClickComponent(Gdx.input.getX(), Gdx.input.getY()));
        }
    }
}
