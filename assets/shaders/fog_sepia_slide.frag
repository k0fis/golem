#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D u_texture;
uniform float u_time;

varying vec2 v_uv;
varying vec4 v_color;

void main() {

    vec4 color = texture2D(u_texture, v_uv);

    // ----------------------------------------
    // 1) Slide factor: 0 → mlha mimo obraz / 1 → celá obrazovka
    // mlha „přijíždí“ z pravé strany
    // ----------------------------------------
    float speed = 0.35;          // jak rychle jede (můžeš změnit)
    float slide = clamp(u_time * speed, 0.0, 1.0);

    // uv.x roste zleva doprava → chceme pravou stranu dřív
    // slide jde od 0→1, čím vyšší slide, tím víc mlhy vlevo
    float sideFog = smoothstep(1.0 - slide, 1.0, v_uv.x);

    // ----------------------------------------
    // 2) Wave fog (původní jemná sin animace)
    // ----------------------------------------
    float waveFog = 0.3 * sin(v_uv.y * 10.0 + u_time * 0.5);

    // ----------------------------------------
    // 3) Hustší mlha u spodku (Machinarium styl)
    // ----------------------------------------
    float verticalFog = smoothstep(0.0, 0.4, 1.0 - v_uv.y) * 0.25;

    // ----------------------------------------
    // 4) Spojení všeho dohromady
    // ----------------------------------------
    float fog = sideFog + waveFog + verticalFog;
    fog = clamp(fog, 0.01, 0.97);

    // ----------------------------------------
    // 5) Sepia tón
    // ----------------------------------------
    vec3 sepia = vec3(
    (color.r * 0.393 + color.g * 0.769 + color.b * 0.189),
    (color.r * 0.349 + color.g * 0.686 + color.b * 0.168),
    (color.r * 0.272 + color.g * 0.534 + color.b * 0.131)
    );

    vec3 tinted = mix(color.rgb, sepia, 0.25);

    // ----------------------------------------
    // 6) Barva mlhy
    // ----------------------------------------
    vec3 fogColor = vec3(0.85, 0.78, 0.60);

    // ----------------------------------------
    // 7) Výsledná barva
    // ----------------------------------------
    vec3 finalColor = mix(tinted, fogColor, fog);

    gl_FragColor = vec4(finalColor, color.a) * v_color;

    //gl_FragColor = texture2D(u_texture, v_uv);
}

