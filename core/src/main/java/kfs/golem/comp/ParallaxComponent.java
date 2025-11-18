package kfs.golem.comp;

import kfs.golem.ecs.KfsComp;

public class ParallaxComponent implements KfsComp {
    public float time = 0.0f;
    public float speed;
    public float amplitude;
    public ParallaxComponent(float speed) { this.speed = speed; this.amplitude = 5f; }
    public ParallaxComponent(float speed, float amplitude) { this.speed = speed; this.amplitude = amplitude; }
}
