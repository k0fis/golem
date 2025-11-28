package kfs.golem.scenes;

import kfs.golem.comp.*;
import kfs.golem.ecs.KfsComp;
import kfs.golem.utils.InvalidShaderName;

public enum ShaderType {
    NONE(null),

    FOG(ShaderFogComponent.class),
    FOG2(ShaderFog2Component.class),
    FOG_ROLLING(ShaderFogRollingComponent.class),
    FOG_SEPIA_SLIDE(ShaderFogSepiaSlideComponent.class),
    FOG_AURA(ShaderFogAuraComponent.class),
    FOG_ROUND_AURA(ShaderFogRoundAuraComponent.class),
    FOG_STARS(ShaderFogStarsComponent.class),

    LAMP(ShaderLampComponent.class),
    OUTLINE(ShaderOutlineComponent.class),
    OUTLINE2(ShaderOutline2Component.class),
    CANDLE_FLICKER(ShaderCandleFlickerComponent.class),
    ;

    public final Class<?extends KfsComp> shaderComponent;

    ShaderType(Class<?extends KfsComp> shaderComponent) {
        this.shaderComponent = shaderComponent;
    }

    public static ShaderType fromShaderName(String shaderName) {
        if (shaderName == null || shaderName.isEmpty()) {
            throw new InvalidShaderName(shaderName);
        }
        try {
            return ShaderType.valueOf(shaderName);
        } catch (IllegalArgumentException e) {
            throw new InvalidShaderName(shaderName, e);
        }
    }
}
