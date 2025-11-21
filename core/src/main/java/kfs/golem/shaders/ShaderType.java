package kfs.golem.shaders;

import com.badlogic.gdx.Gdx;

public enum ShaderType {
    NONE,
    FOG, FOG2, FOG_SEPIA_SLIDE,
    LAMP;

    public static ShaderType fromShaderName(String shaderName, boolean willThrows) {
        if (shaderName == null || shaderName.isEmpty()) {
            if (willThrows) {
                throw new IllegalArgumentException("Invalid shader name: " + shaderName);
            }
            return NONE;
        }
        try {
            return ShaderType.valueOf(shaderName);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid shader type: " + shaderName, e);
        }
    }
}
