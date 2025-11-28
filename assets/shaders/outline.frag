#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_uv;

uniform sampler2D u_texture;
uniform float u_time;
uniform float u_dotSize;     // velikost bodu
uniform vec3  u_dotColor;    // barva tečky
uniform float u_speed;       // rychlost tečky
uniform vec2  u_center;

void main() {
    vec4 base = texture2D(u_texture, v_uv);

    // 1) Použijeme jen masku obrysu — outline = pixely s nějakou alfou
    float outlineMask = base.a;
    if(outlineMask < 0.1) {
        gl_FragColor = vec4(0.0);
        return;
    };

    // 2) Přepočet úhlu pixelu kolem středu
    float angle = atan(v_uv.y - u_center.y, v_uv.x - u_center.x);

    float normAngle = (angle + 3.1415926) / (2.0 * 3.1415926);
    normAngle = mix(normAngle, 0.5, 1.0 - outlineMask);

    // 3) Pozice tečky (jede kolem dokola)
    float dotPos = fract(u_time/u_speed); // hodnota 0.0 - 1.0

    // 4) Jak moc je pixel blízko bodu
    float dist = abs(normAngle - dotPos);
    dist = min(dist, 1.0 - dist); // wrap-around

    float dotIntensity = smoothstep(u_dotSize, 0.0, dist);

    // 5) Výsledná barva = pouze bod
    gl_FragColor = vec4(u_dotColor, dotIntensity * outlineMask);
}

