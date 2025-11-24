#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_uv;

uniform sampler2D u_texture;

uniform float u_time;   // nemusí být, ale nechávám ho
uniform float u_speed;
uniform float u_scale;

// ultrajednoduchý noise – extrémní kontrast
float hash(vec2 p) {
    return fract(sin(dot(p, vec2(37.0, 17.0))) * 43758.5453);
}

void main() {
    vec4 base = texture2D(u_texture, v_uv);

    // posuv UV
    vec2 uv = v_uv * u_scale;
    uv.x += u_time * u_speed;
    uv.y += sin(u_time) * 0.1;

    // BRUTÁLNÍ noise (jasně viditelný)
    float n = hash(uv);

    // udělej čistě bílou viditelnou mlhu
    float fog = step(0.5, n);  // tvrdý kontrast, 50% bílá

    vec3 fogColor = vec3(1.0);

    // silný mix (50% mlha)
    vec3 result = mix(base.rgb, fogColor, fog * 0.7);

    gl_FragColor = vec4(result, 1.0);
}
