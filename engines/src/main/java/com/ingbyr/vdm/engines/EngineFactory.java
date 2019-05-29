package com.ingbyr.vdm.engines;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EngineFactory {
    public static IEngine getInstance(Class<?> clazz) {
        IEngine IEngine = null;
        try {
            IEngine = (IEngine) Class.forName(clazz.getName()).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("Can't create IEngine: {}", clazz.getName());
            log.error(e.toString());
        }
        return IEngine;
    }
}
