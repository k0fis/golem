attribute vec4 a_position;
attribute vec2 a_texCoord0;

uniform vec2 u_resolution;

varying vec2 v_uv;

void main() {
    v_uv = a_texCoord0;

    vec2 clip = (a_position.xy / u_resolution) * 2.0 - 1.0;
    gl_Position = vec4(clip, 0.0, 1.0);
}
