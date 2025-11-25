#ifdef GL_ES
precision mediump float;
#endif

    varying vec2 v_uv;

    uniform sampler2D u_texture;
    uniform float u_time;
    uniform float u_thickness;   // váha detekce obrysu
    uniform float u_dotSize;     // velikost bodu
    uniform vec3  u_dotColor;    // barva tečky

    void main() {
        vec4 base = texture2D(u_texture, v_uv);

        // 1) Použijeme jen masku obrysu — outline = pixely s nějakou alfou
        float outlineMask = base.a;
        if(outlineMask < 0.01) {
            gl_FragColor = vec4(0.0);
            return;
        };

        // 2) Přepočet úhlu pixelu kolem středu
        vec2 center = vec2(0.5, 0.5);
        float angle = atan(v_uv.y - center.y, v_uv.x - center.x);

        float normAngle = (angle + 3.1415926) / (2.0 * 3.1415926);

        // 3) Pozice tečky (jede kolem dokola)
        float dotPos = fract(u_time/10.0); // hodnota 0.0 - 1.0

        // 4) Jak moc je pixel blízko bodu
        float dist = abs(normAngle - dotPos);
        dist = min(dist, 1.0 - dist); // wrap-around

        float dotIntensity = smoothstep(u_dotSize, 0.0, dist);

        // 5) Výsledná barva = pouze bod
        gl_FragColor = vec4(u_dotColor, dotIntensity * outlineMask);
    }
