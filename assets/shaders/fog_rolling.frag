#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_time;

varying vec2 v_uv;
varying vec4 v_color;

// ----------------------------------------------------
// Smoothed noise (fbm-like)
// ----------------------------------------------------
float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);

    // four corners
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));

    // smooth factor
    vec2 u = f * f * (3.0 - 2.0 * f);

    // interpolation
    return mix(a, b, u.x) +
    (c - a) * u.y * (1.0 - u.x) +
    (d - b) * u.x * u.y;
}

// ----------------------------------------------------
// fbm – fractal noise (smooth rolling appearance)
// ----------------------------------------------------
float fbm(vec2 p) {
    float v = 0.0;
    float a = 0.5;

    for(int i = 0; i < 5; i++) {
        v += a * noise(p);
        p *= 2.0;
        a *= 0.5;
    }
    return v;
}

// ----------------------------------------------------
// MAIN
// ----------------------------------------------------
void main() {

    vec4 bg = texture2D(u_texture, v_uv);

    // ------------------------------------------------
    // Rolling fog base movement
    // ------------------------------------------------
    float t = u_time * 0.05;

    // parallax scale
    vec2 uv1 = v_uv * 2.5;
    vec2 uv2 = v_uv * 5.0;
    vec2 uv3 = v_uv * 10.0;

    // rolling flow (horizontal drift)
    uv1.x += t * 0.3;
    uv2.x += t * 0.6;
    uv3.x += t * 1.2;

    // vertical slow breathing
    uv1.y += sin(u_time * 0.2) * 0.02;
    uv2.y += sin(u_time * 0.25) * 0.03;
    uv3.y += sin(u_time * 0.3) * 0.04;

    // swirling effect (adds that cloud-like rollover)
    uv1 += vec2(
    sin((v_uv.y + u_time * 0.1) * 3.0) * 0.02,
    cos((v_uv.x + u_time * 0.1) * 3.0) * 0.01
    );

    // ------------------------------------------------
    // fog layers (fbm)
    // ------------------------------------------------
    float fog1 = fbm(uv1) * 0.6;
    float fog2 = fbm(uv2) * 0.4;
    float fog3 = fbm(uv3) * 0.25;

    float fog = fog1 + fog2 + fog3;

    // clamp for nice density
    fog = smoothstep(0.3, 0.85, fog);

    // final alpha
    float fogAlpha = clamp(fog * 0.85, 0.0, 0.85);

    // fog color
    vec3 fogColor = vec3(0.92, 0.87, 0.82);

    // mix background
    vec3 finalColor = mix(bg.rgb, fogColor, fogAlpha);

    gl_FragColor = vec4(finalColor, bg.a) * v_color;
}
