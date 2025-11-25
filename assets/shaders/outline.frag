#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_uv;

uniform sampler2D u_texture;
uniform float u_time;
uniform float u_speed;
uniform float u_width;        // šířka světelné vlny (0.01 = úzký)
uniform vec3 u_glowColor;     // barva efektu

void main() {
    vec4 base = texture2D(u_texture, v_uv);

    if (base.a < 0.01) discard;

    // pozice světelné vlny podél obvodu
    float pos = fract(u_time * u_speed);

    // vzdálenost aktuálního pixelu od světelné vlny (na U ose)
    float dist = abs(v_uv.x - pos);

    // wrap-around (aby vlnka plynule přešla z 1 -> 0)
    dist = min(dist, 1.0 - dist);

    // tvar vlny – exponenciálně tlumíme
    float glow = exp(-dist * dist / u_width);

    // jemné přidání barvy (0..1)
    vec3 color = base.rgb + glow * u_glowColor;

    gl_FragColor = vec4(color, base.a);
}
