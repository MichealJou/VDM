package com.ingbyr.vdm.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

class ProcessUtilsTest {

    @Test
    void exec() throws IOException, InterruptedException {

        String output = ProcessUtils.exec(
                new File("."),
                Arrays.asList("ls", "-a"));
        Assertions.assertNotEquals(output, "");
    }
}