package com.ingbyr.vdm.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemUtilsTest {

    @Test
    void currentOS() {
        System.out.println("current os: " + SystemUtils.currentOS());
    }
}