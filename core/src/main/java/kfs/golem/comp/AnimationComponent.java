package kfs.golem.comp;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import kfs.golem.ecs.KfsComp;

public class AnimationComponent implements KfsComp {
    public Animation<TextureRegion> animation;
    public float stateTime = 0f;
    public boolean enabled = true;

    public AnimationComponent(Animation<TextureRegion> animation, boolean enabled) {
        this.animation = animation;
        this.enabled = enabled;
    }

}
