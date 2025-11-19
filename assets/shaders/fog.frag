#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;      // background
uniform float u_time;             // global time
uniform float u_introProgress;    // 0 → 1 (slide-in progress)

varying vec2 v_uv;
varying vec4 v_color;


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
// MAIN
// ----------------------------------------------------
void main() {

    vec4 bg = texture2D(u_texture, v_uv);

    // ------------------------------------------------
    // Intro slide-in (z prava → doleva)
    // ------------------------------------------------
    float intro = clamp(u_introProgress, 0.0, 1.0);

    // smoothstep easing
    float p = intro * intro * (3.0 - 2.0 * intro);

    // start = mlha mimo obraz (slide = 1), konec = 0
    float slide = 1.0 - p;


    // ------------------------------------------------
    // Continuous drift mlhy doleva
    // ------------------------------------------------
    // jemný, stálý pohyb – nikdy se nezastaví
    float drift = -u_time * 0.02;


    // ------------------------------------------------
    // Parallax mlha (3 vrstvy)
    // ------------------------------------------------

    // VRSTVA 1 – nejpomalejší
    vec2 uv1 = v_uv * 2.0;
    uv1.x += drift + slide;               // slide-in + drift
    uv1.y += sin(u_time * 0.25) * 0.02;   // jemná turbulence

    float fog1 = smoothstep(0.35, 0.75, noise(uv1));
    float alpha1 = fog1 * 0.18;

    // VRSTVA 2 – střední rychlost
    vec2 uv2 = v_uv * 4.0;
    uv2.x += drift * 1.5 + slide;
    uv2.y += sin(u_time * 0.33) * 0.03;

    float fog2 = smoothstep(0.40, 0.80, noise(uv2));
    float alpha2 = fog2 * 0.25;

    // VRSTVA 3 – nejrychlejší
    vec2 uv3 = v_uv * 8.0;
    uv3.x += drift * 2.0 + slide;
    uv3.y += sin(u_time * 0.41) * 0.035;

    float fog3 = smoothstep(0.45, 0.85, noise(uv3));
    float alpha3 = fog3 * 0.35;


    // ------------------------------------------------
    // Finální alfa mlhy
    // ------------------------------------------------
    float fogAlpha = clamp(alpha1 + alpha2 + alpha3, 0.0, 0.8);

    // fade-in během slide-in
    fogAlpha *= p;


    // ------------------------------------------------
    // Barva mlhy
    // ------------------------------------------------
    vec3 fogColor = vec3(0.90, 0.78, 0.55);


    // ------------------------------------------------
    // Finální barva
    // ------------------------------------------------
    vec3 finalColor = mix(bg.rgb, fogColor, fogAlpha);
    gl_FragColor = vec4(finalColor, bg.a) * v_color;
}
