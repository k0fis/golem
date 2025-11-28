#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_uv;

uniform sampler2D u_texture;

uniform float u_time;
uniform float u_band; // 0.12 (%)
uniform float u_intensity;
uniform float u_detail;
uniform float u_speed;
uniform float u_falloff;
uniform vec3  u_color;

// --- noise --------------------------------------------------------------
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

    vec2 u = f*f*(3.0 - 2.0*f);

    return mix(a, b, u.x)
    + (c - a)*u.y*(1.0 - u.x)
    + (d - b)*u.x*u.y;
}
// ------------------------------------------------------------------------

void main() {
    vec4 base = texture2D(u_texture, v_uv);

    float t = u_time * u_speed;

    // slight swirl around center for "aura rotation"
    vec2 centered = (v_uv - 0.5);
    float angle = atan(centered.y, centered.x);
    float swirl = sin(angle * 3.0 + t * 0.7);

    // animated noise
    float n = noise(v_uv * u_detail + t + swirl * 0.1);

    // original fog shaping
    float fog = smoothstep(0.35, 0.95, n);
    fog *= u_falloff;

    // ------------------------------------------------------------------
    //           **EDGE MASK – aura only near borders (~12%)**
    // ------------------------------------------------------------------

    float border = u_band; // 12% from each side

    // distance from closest edge
    float distLeft   = v_uv.x;
    float distRight  = 1.0 - v_uv.x;
    float distBottom = v_uv.y;
    float distTop    = 1.0 - v_uv.y;

    float edgeDist = min(min(distLeft, distRight), min(distTop, distBottom));

    // edgeMask = 1 near edge, 0 inside
    float edgeMask = 1.0 - smoothstep(border, border + u_band, edgeDist);

    // apply edge mask to fog
    fog *= edgeMask;

    // color and alpha like before (compatible with SRC_ALPHA blend)
    float alpha = fog * u_intensity;
    vec3 effectColor = u_color * fog * u_intensity;

    vec4 fogLayer = vec4(effectColor, alpha);

    // standard alpha blend
    gl_FragColor = base * (1.0 - fogLayer.a) + fogLayer;
}
