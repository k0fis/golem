package kfs.golem.scenes;

import com.badlogic.gdx.math.Vector2;
import kfs.golem.GolemMain;
import kfs.golem.utils.BubbleStyle;

public class C1S2_Bridge extends SceneLoader {

    public C1S2_Bridge(GolemMain golemMain) {
        super(golemMain, "scenes/C1S2_Bridge.json");
    }

    @Override
    public void load() {
        super.load();
        createTimeAfterSubtitlesForNextScene(5, new C1S3_DarkStreet1(golemMain));

        createDialog(" > > ",
            new Vector2(1070, 720), () -> golemMain.loadScene(new C1S3_DarkStreet1(golemMain), 1.5f),
            BubbleStyle.BubbleTail.NONE);

    }

    @Override
    public Class<? extends SceneLoader> getSLClass() {
        return getClass();
    }
}
