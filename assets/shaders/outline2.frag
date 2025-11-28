#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_uv;
uniform sampler2D u_texture;  // Your original texture
uniform float u_time;         // Time in seconds
uniform vec3 u_outlineColor;  // Color for the outline (e.g., vec3(1.0, 0.0, 0.0) for red)
uniform float u_pulseDuration; // Duration of the pulse (e.g., 1.0 second)
uniform float u_pulseInterval; // Interval between pulses (e.g., 5.0 seconds)

void main() {
    vec4 base = texture2D(u_texture, v_uv);

    if(base.a < 0.1) {
        gl_FragColor = vec4(0.0);
        return;
    };

    // 2. Calculate the pulse phase (0.0 to 1.0)
    float pulsePhase = fract(u_time / u_pulseInterval);
    // Normalize the phase to fit within the pulse duration
    float pulseProgress = smoothstep(0.0, u_pulseDuration / u_pulseInterval, pulsePhase);
    // Create a smooth fade-in and fade-out effect
    float pulseIntensity = smoothstep(0.0, 0.1, pulseProgress) * (1.0 - smoothstep(0.9, 1.0, pulseProgress));

    // 3. Apply the outline color ONLY during the pulse
    vec3 finalColor = base.rgb;
    if (pulseIntensity > 0.0) {
        finalColor = mix(base.rgb, u_outlineColor, pulseIntensity * base.a);
    }

    // 4. Output the final color
    gl_FragColor = vec4(finalColor, pulseIntensity);
}
