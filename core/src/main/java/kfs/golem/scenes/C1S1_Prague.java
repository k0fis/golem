package kfs.golem.scenes;

import com.badlogic.gdx.math.Vector2;
import kfs.golem.GolemMain;
import kfs.golem.comp.PositionComponent;
import kfs.golem.comp.SubtitleMultilineComponent;
import kfs.golem.comp.TimerComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.utils.BubbleStyle;

public class C1S1_Prague extends SceneLoader {

    public C1S1_Prague(GolemMain golem) {
        super(golem, "scenes/C1S1_Prague.json");
    }

    @Override
    public void load() {
        super.load();
        createTimeAfterSubtitlesForNextScene(5, new C1S2_Bridge(engine));

        createDialog(" > > ",
            new Vector2(1070, 720), () -> engine.loadScene(new C1S2_Bridge(engine)),
            BubbleStyle.BubbleTail.NONE);


        Entity lamp = textures.get("c1s1-lamp-1");
        if (lamp == null) {
            throw new RuntimeException("lamp is null");
        }

    }

    @Override
    public Class<? extends SceneLoader> getSLClass() {
        return getClass();
    }
}
