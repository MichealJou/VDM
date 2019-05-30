package com.ingbyr.vdm.engines;

import com.ingbyr.vdm.common.DownloadStatus;
import com.ingbyr.vdm.common.ProcessUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractEngine implements IEngine {
    protected volatile boolean stopped = false;
    protected Map<String, String> args = new HashMap<>();
    protected DownloadStatus status = new DownloadStatus("", "0.0", "0KiB/s", "0KiB");

    // Init in constructor
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
        // Add executable native engine binary file name
        cmd.add("." + File.separator + engineType.name);

        args.forEach((k, v) -> {
            if (k.startsWith("-")) cmd.add(k);
            cmd.add(v);
        });

        return cmd;
    }

    protected void stopTask() {
        stopped = false;
    }

    protected String execAndGetOutput() throws IOException, InterruptedException {
        StringBuilder output = new StringBuilder();
        exec(output::append);
        return output.toString();
    }

    protected void exec(Consumer<String> consumer) throws IOException, InterruptedException {
        ProcessUtils.exec(config.enginesDir().toFile(), getCmd(), consumer);
    }

    @Override
    public void setConfig(IEngineConfig config) {
        this.config = config;
    }

    protected abstract void parseConfig();
}
