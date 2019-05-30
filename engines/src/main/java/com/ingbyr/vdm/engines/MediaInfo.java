package com.ingbyr.vdm.engines;

import java.util.List;

public interface MediaInfo {
    String title();

    List<MediaFormat> mediaFormats();
}
