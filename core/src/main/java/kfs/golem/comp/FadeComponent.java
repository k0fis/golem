package kfs.golem.comp;

import com.badlogic.gdx.Screen;
import kfs.golem.ecs.KfsComp;

public class FadeComponent implements KfsComp {
    public enum Mode { NONE, FADE_IN, FADE_OUT }

    public Mode mode = Mode.NONE;
    public float duration = 1f;
    public float time = 0f;
    public float alpha = 0f;

    public Runnable onComplete = null;
}
