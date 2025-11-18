#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
varying vec4 v_color;

uniform sampler2D u_texture;
uniform vec3 u_lightColor;
uniform float u_time;

uniform float u_intensity;
uniform float u_flickerStrength;
uniform vec2  u_ellipseScale;

// ---------- noise / fbm ------------
float hash21(vec2 p){
    p = fract(p*vec2(123.34, 456.21));
    p += dot(p, p+45.32);
    return fract(p.x*p.y);
}

float noise(vec2 p){
    vec2 i = floor(p);
    vec2 f = fract(p);
    float a = hash21(i);
    float b = hash21(i + vec2(1.0, 0.0));
    float c = hash21(i + vec2(0.0, 1.0));
    float d = hash21(i + vec2(1.0, 1.0));
    vec2 u = f*f*(3.0 - 2.0*f);
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float fbm(vec2 p){
    float v = 0.0;
    v += 0.5  * noise(p);
    p *= 2.0;
    v += 0.25 * noise(p);
    p *= 2.0;
    v += 0.125 * noise(p);
    p *= 2.0;
    v += 0.0625 * noise(p);
    return v;
}

// ---------- MAIN -------------------
void main() {
    vec4 tex = texture2D(u_texture, v_texCoords);

    // eliptický tvar
    vec2 c = v_texCoords - vec2(0.5);
    c *= u_ellipseScale;
    float d = length(c) * 2.0;
    float base = tex.a * v_color.a * (1.0 - clamp(d, 0.0, 1.0));

    // ---- REALISTICKÉ FLICKER JAKO SVÍČKA ----

    // 1) pomalé "dýchání" plamene
    float slow = fbm(vec2(u_time * 0.5, 0.0));

    // 2) středně rychlé kolísání (větší turbulence)
    float mid = fbm(v_texCoords * 5.0 + vec2(0.0, u_time * 0.8));

    // 3) rychlé mikroskopické záblesky
    float fast = fbm (v_texCoords * 40.0 + u_time * 4.3);

    float micro = fbm(v_texCoords * 25.0 + u_time * 2.0) * 0.1;

    // kombinace – odladěno na realistické chování
    float flick = 0.7 +                 // základní intenzita
    slow * 0.35 +         // pomalé kolísání
    mid  * 0.25 +         // turbulence
    fast * 0.15 +          // rychlé špičky
    micro;

    flick = mix(1.0, flick, u_flickerStrength);

    float alpha = clamp(base * u_intensity * flick, 0.0, 1.0);

    // lehká teplotní změna barev (teplejší při špičkách)
    vec3 flameColor = u_lightColor * (0.85 + flick * 0.55);  // vyšší flick = teplejší barva

    gl_FragColor = vec4(flameColor * alpha, alpha);
}
