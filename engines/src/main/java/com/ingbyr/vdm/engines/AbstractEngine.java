package com.ingbyr.vdm.engines;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractEngine implements IEngine {
    protected volatile boolean stopped = false;
    protected EngineType engineType;
    protected Map<String, String> args;
    protected IEngineConfig config;

    /**
     * Convert args to real command
     *
     * @return
     */
    protected List<String> getCmd() {
        List<String> cmd = new ArrayList<>();
        args.forEach((k, v) -> {
            if (k.startsWith("-")) cmd.add(k);
            cmd.add(v);
        });
        return cmd;
    }

    protected void stopTask() {
        stopped = false;
    }

    protected abstract void parseConfig();
}
