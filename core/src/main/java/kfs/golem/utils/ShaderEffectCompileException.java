package kfs.golem.utils;

public class ShaderEffectCompileException extends RuntimeException {

    public ShaderEffectCompileException(String frag, String log) {
        super("Shader compile error for ( " + frag + "): " + log);
    }
    public ShaderEffectCompileException(String vert, String frag, String log) {
        super("Shader compile error for ("  + vert+", " + frag +"): " + log);
    }
}
