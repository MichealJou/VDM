package com.ingbyr.vdm.common;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Setter
public final class ProcessUtils {

    private File workDir;
    private Consumer<String> handleOutput;

    private void readOutput(Process process) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            for (String output = reader.readLine(); output != null; output = reader.readLine()) {
                log.debug(output);
                handleOutput.accept(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exec(List<String> command) throws IOException, InterruptedException {
        log.debug("Execute command {} at {}", command, workDir);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(workDir);
        Process process = processBuilder.start();
        readOutput(process);
        // wait for process
        process.waitFor();
    }

    public static void exec(File workDir, List<String> command, Consumer<String> handleOutput) throws IOException, InterruptedException {
        ProcessUtils utils = new ProcessUtils();
        utils.setWorkDir(workDir);
        utils.setHandleOutput(handleOutput);
        utils.exec(command);
    }
}
