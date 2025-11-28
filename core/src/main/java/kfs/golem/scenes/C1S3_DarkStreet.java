package kfs.golem.scenes;

import com.badlogic.gdx.math.Vector2;
import kfs.golem.GolemMain;
import kfs.golem.comp.InteractiveComponent;
import kfs.golem.comp.TextureComponent;
import kfs.golem.ecs.Entity;
import kfs.golem.utils.BubbleStyle;

public class C1S3_DarkStreet extends  SceneLoader {

    public C1S3_DarkStreet(GolemMain golem) {
        super(golem, "scenes/C1S3_DarkStreet.json");
    }


    @Override
    public void load() {
        super.load();

        createDialog(" > > ",
            new Vector2(1400, 950), () -> golemMain.loadScene(new C1S4_DarkStreet(golemMain), 1.5f),
            BubbleStyle.BubbleTail.NONE);

        createTimeAfterSubtitlesForNextScene(5, new C1S4_DarkStreet(golemMain));

        setDefaultActionForLamp("c1s3-lamp-1");
    }

    @Override
    public Class<? extends SceneLoader> getSLClass() {
        return getClass();
    }
}
