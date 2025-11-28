#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_uv;

uniform sampler2D u_texture;

// Effect parameters
uniform float u_time;
uniform float u_intensity;   // 0–1
uniform float u_detail;      // 1–4
uniform float u_speed;       // 0.2–3
uniform float u_falloff;     // 0–1
uniform vec3  u_color;       // efektová barva

// --- SIMPLE NOISE ------------------------------------------------------
float hash(vec2 p)
{
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}

float noise(vec2 p)
{
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
// -----------------------------------------------------------------------

void main()
{
    vec4 base = texture2D(u_texture, v_uv);

    // animated fog
    float t = u_time * u_speed;
    float n = noise(v_uv * u_detail + t);

    // Fog band shaping
    float fog = smoothstep(0.35, 0.95, n);
    fog *= u_falloff;

    // FINAL COLOR: normal alpha (not premultiplied)
    vec3 effectColor = u_color * fog * u_intensity;

    // alpha defines visibility of fog
    float alpha = fog * u_intensity;

    // regular alpha blend-friendly result
    vec4 fogLayer = vec4(effectColor, alpha);

    gl_FragColor = base * (1.0 - fogLayer.a) + fogLayer;
}
