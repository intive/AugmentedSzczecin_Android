package com.blstream.as.debug;

/**
 * Created by sswierczek
 */
public enum BuildType {

    DEBUG("debug"),
    RELEASE("release");

    public final String buildName;

    private BuildType(String buildName) {
        this.buildName = buildName;
    }
}
