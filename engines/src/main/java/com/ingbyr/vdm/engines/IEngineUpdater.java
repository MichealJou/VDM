package com.ingbyr.vdm.engines;

import java.util.concurrent.CompletableFuture;

public interface IEngineUpdater {
    String getUpdateUrl();

    boolean updatesAvailable();

    CompletableFuture<String> downloadNewEngine();
}
