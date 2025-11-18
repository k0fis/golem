#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;  // background
uniform float u_time;
varying vec2 v_uv;

// ----------------------------------------------------
// Simple gradient noise (WebGL1-safe)
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


void main() {

    vec4 bg = texture2D(u_texture, v_uv);

    // --------------------------------------------
    // Parallax mlha (stejné jako předtím)
    // --------------------------------------------
    vec2 uv1 = v_uv * 2.0 + vec2(u_time * 0.02, u_time * 0.01);
    float fog1 = smoothstep(0.35, 0.75, noise(uv1));
    float alpha1 = fog1 * 0.18;

    vec2 uv2 = v_uv * 4.0 + vec2(u_time * 0.06, u_time * 0.03);
    float fog2 = smoothstep(0.40, 0.80, noise(uv2));
    float alpha2 = fog2 * 0.25;

    vec2 uv3 = v_uv * 8.0 + vec2(u_time * 0.12, u_time * 0.05);
    float fog3 = smoothstep(0.45, 0.85, noise(uv3));
    float alpha3 = fog3 * 0.35;

    float fogAlpha = clamp(alpha1 + alpha2 + alpha3, 0.0, 0.8);

    // --------------------------------------------
    // SEPIA / INDUSTRIAL SMOG COLOR
    //
    // Takový „Machinarium gold-smoke“ mix:
    //
    // R: 0.90  – jemně zlatavá
    // G: 0.78  – teplé stíny
    // B: 0.55  – kontrast, rezavý tón
    //
    // Můžeš měnit dle chuti.
    // --------------------------------------------
    vec3 fogColor = vec3(0.90, 0.78, 0.55);

    // --------------------------------------------
    // Mixing: průsvitná sepia mlha
    // --------------------------------------------
    vec3 finalColor = mix(bg.rgb, fogColor, fogAlpha);

    gl_FragColor = vec4(finalColor, 1.0);
}
