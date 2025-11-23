#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_uv;
varying vec4 v_color;

uniform sampler2D u_texture;
uniform float u_brightness;   // 0 = nic, 0.3 = zesvětlení, -0.3 = ztmavení

void main() {
    vec4 color = texture2D(u_texture, v_uv);

    // Barva sprite (batch tint), většinou 1.0
    color *= v_color;

    // Additivní zesvětlení
    color.rgb += u_brightness;

    // Omezit na 0–1
    color.rgb = clamp(color.rgb, 0.0, 1.0);

    gl_FragColor = color;
}
