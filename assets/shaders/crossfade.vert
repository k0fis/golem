attribute vec4 a_position;
attribute vec2 a_texCoord0;

varying vec2 v_uv;

void main() {
    v_uv = a_texCoord0;
    gl_Position = a_position;
}
