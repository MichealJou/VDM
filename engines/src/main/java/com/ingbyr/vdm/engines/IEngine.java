package com.ingbyr.vdm.engines;

import java.io.IOException;

public interface IEngine {
    MediaInfo fetchMediaInfo() throws IOException, InterruptedException;

    String currentVersion() throws IOException, InterruptedException;

    void setConfig(IEngineConfig config);

    void download() throws IOException, InterruptedException;

    void download(String formatID) throws IOException, InterruptedException;

    void downloadPlaylist() throws IOException, InterruptedException;
}
