package com.ingbyr.vdm.common;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
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
public final class ProcessUtils {

    private File workDir;

    private Consumer<String> consumeOutput;

    private Process process;

    private List<String> command;

    @Getter
    @Setter
    private volatile boolean stop = false;

    private void readOutput(Process process) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            for (String output = reader.readLine(); output != null; output = reader.readLine()) {
                if (stop) {
                    process.destroy();
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
        log.debug("[Process-{}] Execute command {} at {}", process.pid(), command, workDir);
        CompletableFuture.runAsync(() -> readOutput(process));
    }

    public void stop() {
        stop = true;
    }

    public static ProcessUtils exec(File workDir,
                                    List<String> command,
                                    Consumer<String> consumeOutput) throws IOException {
        ProcessUtils processUtils = new ProcessUtils();
        processUtils.setConsumeOutput(consumeOutput);
        processUtils.setWorkDir(workDir);
        processUtils.setCommand(command);
        processUtils.exec();
        return processUtils;
    }
}
