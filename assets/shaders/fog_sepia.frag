#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D u_texture0;
uniform vec2 u_resolution;
uniform float u_time;

varying vec2 v_texCoords;

void main() {
    vec2 uv = v_texCoords;

    // základní barva z framebufferu
    vec4 color = texture2D(u_texture0, uv);

    // jemná mlha pomocí sin animace
    float fog = 0.08 * sin(uv.y * 10.0 + u_time * 0.5);

    // více husté u spodku obrazovky
    fog += smoothstep(0.0, 0.4, 1.0 - uv.y) * 0.25;

    // sepia tón
    vec3 sepia = vec3(
    (color.r * 0.393 + color.g * 0.769 + color.b * 0.189),
    (color.r * 0.349 + color.g * 0.686 + color.b * 0.168),
    (color.r * 0.272 + color.g * 0.534 + color.b * 0.131)
    );

    vec3 finalColor = mix(color.rgb, sepia, 0.25);

    // aplikace fog
    finalColor = mix(finalColor, vec3(0.8, 0.75, 0.6), fog);

    gl_FragColor = vec4(finalColor, 1.0);
}
