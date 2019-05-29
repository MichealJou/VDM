package com.ingbyr.vdm.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SystemUtils {
    public static OSType currentOS() {
        String osName = System.getProperty("os.name", "unknown");
        if (osName.startsWith("Win")) return OSType.Win;
        else if (osName.startsWith("Linux")) return OSType.Linux;
        else if (osName.startsWith("Mac"))  return OSType.Mac;
        else {
            log.error("Not supported os: " + osName);
            throw new RuntimeException("Not supported os: " + osName);
        }
    }
}
