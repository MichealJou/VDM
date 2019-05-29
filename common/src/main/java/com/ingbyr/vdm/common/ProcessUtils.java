package com.ingbyr.vdm.common;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Slf4j
@Setter
public final class ProcessUtils {

    private File workDir;
    private StringBuilder processOutput;
    private static final String lineSeparator = System.lineSeparator();

    private void readOutput(Process process) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            for (String output = reader.readLine(); output != null; output = reader.readLine()) {
                log.debug(output);
                processOutput.append(lineSeparator);
                processOutput.append(output);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String exec(List<String> command) throws IOException, InterruptedException {
        log.debug("exec {} at {}", command, workDir);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(workDir);
        Process process = processBuilder.start();
        readOutput(process);
        // wait for process
        process.waitFor();
        return processOutput.toString();
    }

    public static String exec(File workDir, List<String> command) throws IOException, InterruptedException {
        ProcessUtils utils = new ProcessUtils();
        utils.setWorkDir(workDir);
        utils.setProcessOutput(new StringBuilder());
        return utils.exec(command);
    }
}
