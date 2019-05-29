package com.ingbyr.vdm.engines.external;

import com.ingbyr.vdm.common.Proxy;
import com.ingbyr.vdm.engines.AbstractEngine;
import com.ingbyr.vdm.engines.EngineType;
import com.ingbyr.vdm.engines.IEngineConfig;

public class EngineYoutubedl extends AbstractEngine {

    public EngineYoutubedl(IEngineConfig config) {
        this.config = config;
        this.engineType = EngineType.YOUTUBE_DL;
        this.parseConfig();
    }

    @Override
    public void getMediaInfoInJson() {

    }

    @Override
    public void downloadMedia() {

    }

    @Override
    public void downloadMediaAsPlaylist() {

    }

    @Override
    public void currentVersion() {

    }

    @Override
    protected void parseConfig() {
        config.proxy().ifPresent(this::addProxy);
    }

    private void addProxy(Proxy proxy) {
        switch (proxy.type) {
            case SOCKS5:
                args.put("--proxy", String.format("socks5://%s:%s", proxy.ip, proxy.port));
            case HTTP:
                args.put("--proxy", String.format("%s:%s", proxy.ip, proxy.port));
        }
    }
}
