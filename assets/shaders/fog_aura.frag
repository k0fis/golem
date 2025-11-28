// fullscreen fragment shader (GLSL ES 1.0 / WebGL / OpenGL ES 2.0)
precision mediump float;
varying vec2 v_uv;

// uniformy: předat ze svého render pipeline
uniform float u_time;           // sekundy
uniform vec2  u_resolution;     // rozlišení (px)
uniform float u_ceilingY;       // 0..1 kde je strop (1.0 = top of screen)
uniform float u_intensity;      // celkové zesílení efektu
uniform vec3  u_color;          // barva mlhy (např. vec3(0.4,0.6,1.0))
uniform float u_speed;          // rychlost posunu noise
uniform float u_detail;         // detail (počet octáv v fbm, 0.0 - 4.0)
uniform float u_falloff;        // jak rychle klesá od stropu (větší = rychlejší úbytek)

//
// --- jednoduché value noise + fbm ---
// Source: malé hash + smoothstep interpolation
//
float hash(vec2 p) {
    p = vec2(dot(p, vec2(127.1,311.7)),
    dot(p, vec2(269.5,183.3)));
    return fract(sin(p.x + p.y) * 43758.5453123);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    // smooth interp
    vec2 u = f*f*(3.0-2.0*f);
    float a = hash(i + vec2(0.0,0.0));
    float b = hash(i + vec2(1.0,0.0));
    float c = hash(i + vec2(0.0,1.0));
    float d = hash(i + vec2(1.0,1.0));
    return mix(mix(a,b,u.x), mix(c,d,u.x), u.y);
}

float fbm(vec2 p, float octaves) {
    float v = 0.0;
    float amp = 0.5;
    float freq = 1.0;
    // clamp octaves reasonable
    int oct = int(clamp(floor(octaves), 1.0, 6.0));
    for (int i = 0; i < 6; i++) {
        if(i >= oct) break;
        v += amp * noise(p * freq);
        freq *= 2.0;
        amp *= 0.5;
    }
    return v;
}

void main() {
    // normalized coords (0..1)
    vec2 uv = v_uv;
    // flip Y if needed: assume v_uv.y==0 bottom, 1 top; chceme efekt pod stropem -> blízko top (y=1)
    float y = uv.y;

    // vzdálenost od stropu (0 = přímo u stropu)
    float d = max(0.0, u_ceilingY - y); // pokud strop=1.0 a y=0.95 => d=0.05
    // kontrola falloff mapy: d_exp vyšší -> rychlejší úbytek
    float d_exp = pow(d * u_resolution.y, u_falloff * 0.001 + 0.05); // škálovat do px a pak malá korekce
    // základní mask (jemný gradient, aby mlha byla blízko stropu)
    float baseMask = smoothstep(0.0, 0.9, 1.0 - d_exp); // 1 u stropu, 0 dál

    // noise coordinates: roztáhnout po obrazovce, posun v čase
    vec2 ncoord = vec2(uv.x * 2.0, (1.0 - (y / max(0.001, u_ceilingY))) * 3.0);
    ncoord += vec2(u_time * u_speed * 0.05, u_time * u_speed * 0.02);

    // základní fbm (jemný závoj)
    float haze = fbm(ncoord * 0.6, u_detail) * 0.5 + 0.1;
    haze *= baseMask;
    haze *= u_intensity;

    // dynamické pásy/proudy (tenčí struktury)
    vec2 streakCoord = vec2(uv.x * 6.0, (1.0 - y/u_ceilingY) * 4.0);
    streakCoord += vec2(-u_time * u_speed * 0.15, u_time * u_speed * 0.02);
    float streaks = fbm(streakCoord, max(u_detail-1.0,1.0));
    // zvýrazníme pruhy s ostřejším thresholdem
    float band = smoothstep(0.55, 0.75, streaks) * 0.8;
    band *= pow(baseMask, 1.5);
    band *= u_intensity * 0.7;

    // jemné vertikální pohyby (vlnění)
    float wisps = fbm(vec2(uv.x * 1.5, u_time * 0.2 + y * 4.0), 3.0) * 0.25;
    wisps *= baseMask;

    // kombinace
    float alpha = clamp(haze * 0.6 + band + wisps, 0.0, 1.0);

    // barevnost: modrý tón, ale trochu teplejší u okrajů
    vec3 color = u_color;
    // drobné barevné variace pro hloubku
    color *= mix(1.0, 1.1 + (fbm(ncoord*1.2, 3.0)-0.5)*0.15, 0.6);

    // finální výstup
    gl_FragColor = vec4(color * alpha, alpha);
}

