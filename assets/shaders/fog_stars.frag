#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_uv;

uniform sampler2D u_texture;
uniform float u_time;
uniform float u_band;
uniform float u_intensity;  // síla efektu
uniform float u_density;    // počet hvězd
uniform float u_speed;      // blikání
uniform vec3  u_color;      // barva hvězd

// distance from center for hex/star
float hexShape(vec2 p, float radius) {
    p = abs(p);
    return max(dot(p, normalize(vec2(1.0, sqrt(3.0)))), p.y) - radius;
}

// random utility
float rand(vec2 co) { return fract(sin(dot(co.xy ,vec2(12.9898,78.233)))*43758.5453); }

// 2D rotation
vec2 rot(vec2 uv, float a) {
    float c = cos(a);
    float s = sin(a);
    return vec2(uv.x * c - uv.y * s, uv.x * s + uv.y * c);
}

void main() {
    vec4 base = texture2D(u_texture, v_uv);

    vec3 sparkleColor = vec3(0.0);
    float sparkleAlpha = 0.0;

    // okraj maska 12%
    float border = u_band;
    float distLeft   = v_uv.x;
    float distRight  = 1.0 - v_uv.x;
    float distBottom = v_uv.y;
    float distTop    = 1.0 - v_uv.y;
    float edgeDist = min(min(distLeft, distRight), min(distTop, distBottom));
    float edgeMask = 1.0 - smoothstep(border, border + u_band, edgeDist);

    // loop přes „náhodné hvězdy“ – limit na ~60 pro výkon
    float count = u_density * 20.0;
    for(float i=0.0;i<60.0;i++){
        if(i>count) break;
        vec2 seed = vec2(i*12.31, i*91.17);
        vec2 pos = vec2(rand(seed), rand(seed+4.0));

        // pouze pokud v edge oblasti
        float dEdge = min(min(pos.x,1.0-pos.x),min(pos.y,1.0-pos.y));
        if(dEdge>border) continue;

        // per-pixel distortion (refract-like)
        vec2 diff = v_uv - pos;
        float n = sin(u_time*u_speed + rand(seed+10.0)*6.28)*0.002;
        diff += diff*vec2(n,-n);

        // rotate for hex/star shape
        diff = rot(diff, rand(seed+7.0)*6.2831);

        float size = 0.004 + rand(seed+7.0)*0.006;
        float s = 1.0 - smoothstep(0.0, size, hexShape(diff*60.0, 0.5));

        // twinkle
        float tw = sin(u_time*u_speed + rand(seed+20.0)*6.28);
        tw = pow(tw*0.5+0.5,3.0);

        float amount = s*tw*edgeMask;

        sparkleColor += u_color*amount;
        sparkleAlpha += amount;
    }

    sparkleColor *= u_intensity;
    sparkleAlpha *= u_intensity;

    vec4 sparkles = vec4(sparkleColor, sparkleAlpha);

    gl_FragColor = base*(1.0 - sparkles.a) + sparkles;
}
