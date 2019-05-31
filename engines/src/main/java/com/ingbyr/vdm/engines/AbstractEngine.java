package com.ingbyr.vdm.engines;

import com.ingbyr.vdm.common.DownloadStatus;
import com.ingbyr.vdm.common.ProcessUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.function.Consumer;

@Slf4j
public abstract class AbstractEngine implements IEngine {

    private volatile boolean running = false;

    protected Map<String, String> args = new HashMap<>();

    protected DownloadStatus status = new DownloadStatus("", "0.0", "0KiB/s", "0KiB");

    protected BlockingQueue<DownloadStatus> statusBlockingQueue = new SynchronousQueue<>(); // Download output status

    // Init in subclass's constructor
    protected EngineType engineType;

    protected Consumer<String> parseDownloadOutput;

    // Init by setter
    protected IEngineConfig config;

    /**
     * Convert args to real command
     *
     * @return
     */
    private List<String> getCmd() {
        List<String> cmd = new ArrayList<>();
        // Add executable native engine binary file path
        cmd.add(config.engine().toString());
        args.forEach((k, v) -> {
            if (k.startsWith("-")) cmd.add(k);
            cmd.add(v);
        });
        return cmd;
    }

    protected CompletableFuture<Process> exec(Consumer<String> consumerWhenRunning) throws IOException {
        running = true;
        ProcessUtils p = ProcessUtils.exec(config.enginesDir().toFile(), getCmd(), consumerWhenRunning);
        CompletableFuture<Process> result = p.getProcess().onExit();
        result.thenAccept(process -> {
            running = false;
            log.debug("[Process-{}] Finished job", process.pid());
        });
        return result;
    }

    @Override
    public void setConfig(IEngineConfig config) {
        this.config = config;
    }

    @Override
    public BlockingQueue<DownloadStatus> getDownloadStatusQueue() {
        running = true;
        return statusBlockingQueue;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void stop() {
        running = false;
    }

    protected abstract void parseConfig();
}
