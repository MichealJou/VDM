package com.ingbyr.vdm.engines;

public enum EngineType {
    YOUTUBE_DL("youtube-dl");

    public final String name;

    EngineType(String name) {
        this.name = name;
    }
}
