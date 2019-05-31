package com.ingbyr.vdm.engines;

import com.ingbyr.vdm.common.DownloadStatus;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface IEngine {
    CompletableFuture<Void> fetchMediaInfo(Consumer<MediaInfo> consumer) throws IOException, InterruptedException;

    CompletableFuture<Process> currentVersion(Consumer<String> consumer) throws IOException, InterruptedException;

    void setConfig(IEngineConfig config);

    void download() throws IOException, InterruptedException;

    void download(String formatID) throws IOException, InterruptedException;

    void downloadPlaylist() throws IOException, InterruptedException;

    BlockingQueue<DownloadStatus> getDownloadStatusQueue();

    boolean isRunning();

    void stop();
}
