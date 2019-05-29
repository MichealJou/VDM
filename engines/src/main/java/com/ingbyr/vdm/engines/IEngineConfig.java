package com.ingbyr.vdm.engines;

import com.ingbyr.vdm.common.Cookie;
import com.ingbyr.vdm.common.DownloadType;
import com.ingbyr.vdm.common.Proxy;

import java.util.Optional;

public interface IEngineConfig {
    String enginePath();

    String mediaUrl();

    DownloadType downloadType();

    boolean downloadDefualtFormat();

    String storagePath();

    Optional<Cookie> cookie();

    Optional<Proxy> proxy();

    Optional<String> formatId();
}
