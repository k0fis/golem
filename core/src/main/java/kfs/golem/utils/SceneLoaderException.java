package kfs.golem.utils;

public class SceneLoaderException extends RuntimeException{

    public SceneLoaderException(String path, Exception e) {
        super("Failed to load scene "+path, e);
    }
}
