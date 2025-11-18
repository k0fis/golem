#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_time;

varying vec2 v_uv;

// ----------------------------------------------------
// Simple gradient noise
// ----------------------------------------------------
float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);

    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));

    vec2 u = f * f * (3.0 - 2.0 * f);

    return mix(a, b, u.x) +
    (c - a) * u.y * (1.0 - u.x) +
    (d - b) * u.x * u.y;
}

// ----------------------------------------------------
// Paper fiber noise
// ----------------------------------------------------
float fibers(vec2 uv) {
    float f1 = noise(uv * 40.0);
    float f2 = noise(uv * vec2(150.0, 4.0));
    return (f1 * 0.6 + f2 * 0.4);
}

void main() {

    vec4 base = texture2D(u_texture, v_uv);

    // --------------------------------------------
    // Grain (animated)
    // --------------------------------------------
    float grain = noise(v_uv * 600.0 + u_time * 0.4);
    grain = grain * 0.12 - 0.06;

    // --------------------------------------------
    // Fibers
    // --------------------------------------------
    float fib = fibers(v_uv);
    fib = (fib - 0.5) * 0.20;

    // --------------------------------------------
    // Soft vignette
    // --------------------------------------------
    vec2 c = v_uv * 2.0 - 1.0;
    float vignette = smoothstep(1.1, 0.4, dot(c, c));

    // --------------------------------------------
    // Combine
    // --------------------------------------------
    vec3 paper = base.rgb;
    paper += grain;
    paper += fib;
    paper *= mix(1.0, vignette, 0.25);

    gl_FragColor = vec4(paper, base.a);
}
