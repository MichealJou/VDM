package com.ingbyr.vdm.engines;

public interface IEngine {
    void getMediaInfoInJson();

    void downloadMedia();

    void downloadMediaAsPlaylist();

    void currentVersion();
}
