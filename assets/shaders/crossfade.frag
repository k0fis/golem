#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_uv;

uniform sampler2D u_texA;   // výstup scény A
uniform sampler2D u_texB;   // výstup scény B
uniform float u_alpha;      // 0 = jen A, 1 = jen B

void main() {
    vec4 a = texture2D(u_texA, v_uv);
    vec4 b = texture2D(u_texB, v_uv);

    gl_FragColor = mix(a, b, u_alpha);
}
