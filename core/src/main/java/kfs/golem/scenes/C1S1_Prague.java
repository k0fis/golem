package kfs.golem.scenes;

import com.badlogic.gdx.math.Vector2;
import kfs.golem.GolemMain;
import kfs.golem.utils.BubbleStyle;

public class C1S1_Prague extends SceneLoader {

    public C1S1_Prague(GolemMain golem) {
        super(golem, "scenes/C1S1_Prague.json");
    }

    @Override
    public void load() {
        super.load();
        createTimeAfterSubtitlesForNextScene(5, new C1S2_Bridge(golemMain));

        createDialog(" > > ", new Vector2(1070, 720), () ->
                golemMain.loadScene(new C1S2_Bridge(golemMain), 1.5f), BubbleStyle.BubbleTail.NONE);


    }

    @Override
    public Class<? extends SceneLoader> getSLClass() {
        return getClass();
    }
}
