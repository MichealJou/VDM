package com.ingbyr.vdm.engines;

import com.ingbyr.vdm.common.DownloadStatus;

import java.util.function.Consumer;

public interface IEngineOutputConsumer {

    void setDownloadStatusConsumer(Consumer<DownloadStatus> consumer);

    Consumer<String> getDownloadOutputConsumer();

    Consumer<String> getGeneralOutputConsumer();

    String getOutput();
}
