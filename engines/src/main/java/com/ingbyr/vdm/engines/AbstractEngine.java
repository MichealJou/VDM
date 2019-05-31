package com.ingbyr.vdm.engines;

import com.ingbyr.vdm.common.DownloadStatus;
import com.ingbyr.vdm.common.ProcessHelper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Slf4j
public abstract class AbstractEngine implements IEngine {

    protected Map<String, String> args = new LinkedHashMap<>();

    protected DownloadStatus status = new DownloadStatus("", "0.0", "0KiB/s", "0KiB");

    // Init in subclass's constructor
    protected EngineType engineType;

    protected IEngineOutputConsumer outputConsumer;

    // Init by setter
    protected IEngineConfig config;

    // Init in runtime
    private Optional<ProcessHelper> processHelperOptional = Optional.empty();

    /**
     * Convert args to real command
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

    protected CompletableFuture<Void> exec(Consumer<String> consumerWhenRunning) throws IOException {
        ProcessHelper ph = ProcessHelper.exec(config.enginesDir().toFile(), getCmd(), consumerWhenRunning);
        processHelperOptional = Optional.of(ph);
        CompletableFuture<Process> result = ph.getProcess().onExit();

        // Process is finished
        return result.thenAccept(process -> {
            log.debug("[Process-{}] Finished job", process.pid());
            processHelperOptional = Optional.empty();
        });
    }

    @Override
    public void setConfig(IEngineConfig config) {
        this.config = config;
    }

    @Override
    public boolean isRunning() {
        return processHelperOptional.isPresent();
    }

    @Override
    public void stop() {
        processHelperOptional.ifPresent(ProcessHelper::stop);
    }

    protected abstract void parseConfig();
}
