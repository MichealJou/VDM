package com.ingbyr.vdm.engines;

import com.ingbyr.vdm.common.DownloadStatus;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface IEngine {
    CompletableFuture<Void> fetchMediaInfo(Consumer<MediaInfo> consumer) throws IOException, InterruptedException;

    CompletableFuture<Void> currentVersion(Consumer<String> consumer) throws IOException, InterruptedException;

    void setConfig(IEngineConfig config);

    CompletableFuture<Void> download(Consumer<DownloadStatus> downloadStatusConsumer) throws IOException;

    CompletableFuture<Void> download(Consumer<DownloadStatus> downloadStatusConsumer, String formatID) throws IOException;

    void downloadPlaylist() throws IOException, InterruptedException;

    boolean isRunning();

    void stop();
}
