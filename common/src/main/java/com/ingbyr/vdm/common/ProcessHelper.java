package com.ingbyr.vdm.common;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Slf4j
@Data
public final class ProcessHelper {

    private File workDir;

    private Consumer<String> consumeOutput;

    private Process process;

    private ProcessHandle processHandle;

    private List<String> command;

    private volatile boolean stop = false;

    private void readOutput(Process process) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            for (String output = reader.readLine(); output != null; output = reader.readLine()) {
                if (stop) {
                    log.debug("[Process-{}] try to stop", process.pid());
                    processHandle.destroy();
                    break;
                }
                log.debug("[Process-{}] {}", process.pid(), output);
                consumeOutput.accept(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exec() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(workDir);
        process = processBuilder.start();
        processHandle = process.toHandle();
        log.debug("[Process-{}] Execute command {} at {}", process.pid(), command, workDir);
        CompletableFuture.runAsync(() -> readOutput(process));
    }

    public void stop() {
        stop = true;
        if (process.isAlive()) {
            processHandle.destroy();
        }
    }

    public static ProcessHelper exec(File workDir,
                                     List<String> command,
                                     Consumer<String> consumeOutput) throws IOException {
        ProcessHelper processHelper = new ProcessHelper();
        processHelper.setConsumeOutput(consumeOutput);
        processHelper.setWorkDir(workDir);
        processHelper.setCommand(command);
        processHelper.exec();
        return processHelper;
    }
}
