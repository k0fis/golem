#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_uv;
varying vec4 v_color;

uniform sampler2D u_texture;
uniform float u_time;

float rand1(float x) {
    return fract(sin(x) * 43758.5453123);
}

void main() {
    vec2 uv = v_uv;

    // jemné kroutění
    float wobble = sin(u_time * 5.0 + uv.y * 10.0) * 0.015;
    uv.x += wobble;

    float stretch = sin(u_time * 7.0 + uv.x * 4.0) * 0.02;
    uv.y += stretch;

    vec4 c = texture2D(u_texture, uv);

    if (c.a < 0.01) discard;

    // simulace náhodného flickeru – místo floor používáme časové „skoky“
    float flickA = 0.9 + 0.1 * sin(u_time * 6.0);
    float flickB = 0.85 + 0.15 * rand1(floor(u_time * 15.0 + 0.5));

    float flicker = (flickA + flickB) * 0.5;
    c.rgb *= flicker;

    gl_FragColor = c * v_color;
}
