package kfs.golem.comp;

import kfs.golem.ecs.Entity;
import kfs.golem.ecs.KfsComp;
import kfs.golem.ecs.KfsWorld;

public class ShaderBrightenComponent implements KfsComp, PostEffectComponent.PecParams {

    private final KfsWorld world;

    public enum State {
        Idle,
        FadingIn,
        FadingOut
    }

    public float brightness = 0f;      // aktuální hodnota shaderu
    public float target = 0f;          // cílová hodnota (0 = tma, 1 = světlo)
    public float speed = 1f;           // kolik jednotek brightness / sekundu
    public State state = State.Idle;

    public ShaderBrightenComponent(KfsWorld world) {
        this.world = world;
    }

    @Override
    public void setUniforms(Entity entity, PostEffectComponent pec, float delta) {
        pec.shader.setUniformf("u_brightness", brightness);
    }

    public void fadeIn() {
        fadeIn(0.25f, 0.5f);
    }

    public void fadeIn(float targetBrightness, float speed) {
        this.target = targetBrightness;
        this.speed = speed;
        this.state = State.FadingIn;
    }

    public void fadeOut() {
        fadeOut(0f, 0.5f);
    }

    public void fadeOut(float targetBrightness, float speed) {
        this.target = targetBrightness;
        this.speed = speed;
        this.state = State.FadingOut;
    }
}
