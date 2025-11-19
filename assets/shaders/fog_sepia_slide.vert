attribute vec4 a_position;
attribute vec2 a_texCoord0;
attribute vec4 a_color;

uniform vec2 u_resolution;

varying vec2 v_uv;
varying vec4 v_color;

void main() {
    v_uv = a_texCoord0;
    v_color = a_color;

    // Convert pixel space → clip space
    vec2 pos = a_position.xy / u_resolution * 2.0 - 1.0;

    gl_Position = vec4(pos, 0.0, 1.0);
}
