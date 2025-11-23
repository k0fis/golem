package kfs.golem.utils;

public class InvalidShaderName extends RuntimeException {

    public InvalidShaderName(String name, Exception cause) {
        super("Invalid shader name: " + name, cause);
    }

    public InvalidShaderName(String name) {
        super("Invalid shader name: " + name);
    }
}
