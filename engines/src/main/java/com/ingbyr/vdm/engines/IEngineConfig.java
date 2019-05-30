package com.ingbyr.vdm.engines;

import com.ingbyr.vdm.common.Cookie;
import com.ingbyr.vdm.common.DownloadType;
import com.ingbyr.vdm.common.Proxy;

import java.nio.file.Path;
import java.util.Optional;

public interface IEngineConfig {
    Path engine();

    Path enginesDir();

    String mediaUrl();

    DownloadType downloadType();

    boolean skippFormatChoice();

    String storagePath();

    Optional<Cookie> cookie();

    Optional<Proxy> proxy();

    Optional<String> formatId();
}
