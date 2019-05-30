package com.ingbyr.vdm.engines.external;

import com.ingbyr.vdm.common.Cookie;
import com.ingbyr.vdm.common.DownloadType;
import com.ingbyr.vdm.common.Proxy;
import com.ingbyr.vdm.engines.EngineFactory;
import com.ingbyr.vdm.engines.IEngine;
import com.ingbyr.vdm.engines.IEngineConfig;
import com.ingbyr.vdm.engines.MediaInfo;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

class EngineYoutubedlTest {

    private Path pwd = Paths.get(System.getProperty("user.dir"));
    private Path tmp = pwd.getParent().resolve("tmp");
    private Path enginesDir = pwd.getParent().resolve("native").resolve("engine");
    private Path engine = enginesDir.resolve("youtube-dl");
    private IEngine e = EngineFactory.getInstance(EngineYoutubedl.class);

    class TestEngineConfig implements IEngineConfig {

        @Override
        public Path engine() {
            return engine;
        }

        @Override
        public Path enginesDir() {
            return enginesDir;
        }

        @Override
        public String mediaUrl() {
            return "https://www.youtube.com/watch?v=eCUsZ-Rg4eM";
        }

        @Override
        public DownloadType downloadType() {
            return DownloadType.SINGLE;
        }

        @Override
        public boolean skippFormatChoice() {
            return false;
        }

        @Override
        public String storagePath() {
            return tmp.toString();
        }

        @Override
        public Optional<Cookie> cookie() {
            return Optional.empty();
        }

        @Override
        public Optional<Proxy> proxy() {
            Proxy proxy = Proxy.builder().ip("127.0.0.1").port("1080").type(Proxy.ProxyType.SOCKS5).build();
            return Optional.of(proxy);
        }

        @Override
        public Optional<String> formatId() {
            return Optional.empty();
        }
    }

    @Test
    void currentVersion() throws IOException, InterruptedException {
        e.setConfig(new TestEngineConfig());
        e.currentVersion();
    }

    @Test
    void fetchMediaInfo() throws IOException, InterruptedException {
        e.setConfig(new TestEngineConfig());
        MediaInfo info = e.fetchMediaInfo();
        System.out.println(info);
    }

    @Test
    void downloadMedia() throws IOException, InterruptedException {
        e.setConfig(new TestEngineConfig());
        e.download();
    }
}