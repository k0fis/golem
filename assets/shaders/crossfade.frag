#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_textureA;   // starý obraz
uniform sampler2D u_textureB;   // nový obraz
uniform float u_fade;           // 0 → A, 1 → B

varying vec2 v_uv;
varying vec4 v_color;

void main() {
    vec4 a = texture2D(u_textureA, v_uv);
    vec4 b = texture2D(u_textureB, v_uv);

    gl_FragColor = mix(a, b, u_fade) * v_color;;
}
