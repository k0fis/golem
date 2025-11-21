// lamp_flicker.frag
#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_uv;
varying vec4 v_color;

uniform sampler2D u_texture;

// čas v sekundách (posílej totaltime)
uniform float u_time;
// intenzita mihotání (0 = bez, ~0.2..1.0 normálně)
uniform float u_intensity;
// rychlost mihotání
uniform float u_speed;
// jak „velký“ šum (menší = detailnější / rychlejší)
uniform float u_noiseScale;
// barevný tint lampy (alpha pro multiplikaci)
uniform vec3 u_colorTint;

// jednoduchý hash -> pseudo-noise
float hash(vec2 p) {
    // konstanty z běžných hash funkcí
    p = mod(p, 1e4);
    float h = dot(p, vec2(127.1, 311.7));
    return fract(sin(h) * 43758.5453123);
}

// smooth noise (interpolovaný)
float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    // smoothstep fade
    vec2 u = f * f * (3.0 - 2.0 * f);

    float a = hash(i + vec2(0.0,0.0));
    float b = hash(i + vec2(1.0,0.0));
    float c = hash(i + vec2(0.0,1.0));
    float d = hash(i + vec2(1.0,1.0));

    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

// fractal noise (few octaves)
float fbm(vec2 p) {
    float v = 0.0;
    float amp = 0.5;
    float freq = 1.0;
    for (int i = 0; i < 4; i++) {
        v += amp * noise(p * freq);
        freq *= 2.0;
        amp *= 0.5;
    }
    return v;
}

void main() {
    vec4 base = texture2D(u_texture, v_uv) * v_color;

    // pozice pro šum: kombinace UV a času
    vec2 p = v_uv * u_noiseScale + vec2(u_time * u_speed, u_time * u_speed * 0.37);

    // hlavní flicker signál (0..1)
    float flick = fbm(p);

    // posuníme a zvýrazníme kontrast (získáme nepravidelné silné pulzy)
    flick = smoothstep(0.25, 0.9, flick);

    // malý pulz + jemné fluktuace
    float slowPulse = 0.5 + 0.5 * sin(u_time * (0.3 + 0.2 * hash(vec2(u_time))));
    float combined = mix(flick, slowPulse, 0.3);

    // výsledná multiplikativní intenzita: 1 ± u_intensity
    float brightness = 1.0 + (combined - 0.5) * 2.0 * u_intensity;

    // barevný tint (např. teplá žlutá)
    vec3 tint = u_colorTint;

    vec3 color = base.rgb * brightness * tint;

    // zachovej původní alfa
    float alpha = base.a;

    gl_FragColor = vec4(color, alpha);
}
