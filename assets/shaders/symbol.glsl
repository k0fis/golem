#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;     // textura s obrysem
uniform float u_time;            // čas v sekundách
uniform float u_fade;
uniform float u_duration;
uniform float u_fillProgress;    // 0.0–1.0 progres výplně (během 5s)
uniform vec2 u_resolution;       // velikost textury

varying vec2 v_texCoords;
varying vec4 v_color;

//
// Jednoduchý 2D noise
//
float hash(vec2 p){
    p = fract(p * vec2(123.34, 345.45));
    p += dot(p, p + 34.345);
    return fract(p.x * p.y);
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

void main()
{
    vec4 base = texture2D(u_texture, v_texCoords);

    if (u_time > u_fade + u_duration) {
        gl_FragColor = vec4(0.0);
        return;
    };
    if (u_time > u_duration) {
        float fade =  smoothstep(0.0, 1.0, base.a / (u_time-u_duration) / u_fade);
        gl_FragColor = vec4(base.rgb, fade);
        return;
    };

    // Outline mask (bila = outline)
    float outlineMask = base.a;     // idealne alpha, ale muze byt R

    // Distancni offset od okraje outline (simulace）
    float dist = texture2D(u_texture, v_texCoords).r;

    // Šíření výplně směrem dovnitř symbolu
    float fill = smoothstep(0.0, 1.0, u_fillProgress - (1.0 - dist));

    // Tekutý kov noise (warping)
    float n = noise(v_texCoords * 8.0 + u_time * 0.7);
    float n2 = noise(v_texCoords * 4.0 - u_time * 0.4);

    float metalMix = clamp(fill + n * 0.2 + n2 * 0.15, 0.0, 1.0);

    // ČERVENO-ZLATÁ barva (metal look)
    vec3 colGold  = vec3(1.0, 0.8, 0.2);
    vec3 colRed   = vec3(1.0, 0.1, 0.05);
    vec3 colFinal = mix(colRed, colGold, metalMix);

    // Výsledek výplně
    vec3 fillColor = colFinal * fill;

    // Outline fade-out po dokončení
    float outlineFade = smoothstep(1.0, 0.1, u_fillProgress);

    vec3 outlineColor = vec3(1.0, 0.8, 0.3);  // zlato
    vec3 outline = outlineColor * outlineMask * outlineFade;

    // Kombinace
    vec3 final = fillColor + outline;
    gl_FragColor = vec4(final, base.a);
}
