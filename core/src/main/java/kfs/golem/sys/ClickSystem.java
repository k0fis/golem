package kfs.golem.sys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import kfs.golem.GolemMain;
import kfs.golem.comp.ClickComponent;
import kfs.golem.ecs.KfsSystem;

public class ClickSystem implements KfsSystem {

    private static final long CLICK_TIME_RETENTION = 1000;

    private final GolemMain golemMain;

    public ClickSystem(GolemMain golemMain) {
        this.golemMain = golemMain;
    }

    long lastClickTime = 0;

    @Override
    public void update(float delta) {
        long clickTime = System.currentTimeMillis();
        if (clickTime - lastClickTime < CLICK_TIME_RETENTION) return;
        if (Gdx.input.isTouched()) {
            lastClickTime = clickTime;
            Vector2 click = new Vector2(Gdx.input.getX(), (Gdx.graphics.getHeight() - Gdx.input.getY()));
            Gdx.app.log("ClickSystem", "Click "+click);
            golemMain.world.addComponent(golemMain.world.createEntity(), new ClickComponent(click));
        }
    }
}
