package com.ingbyr.vdm.engines.external;

import com.ingbyr.vdm.common.Cookie;
import com.ingbyr.vdm.common.DownloadType;
import com.ingbyr.vdm.common.Proxy;
import com.ingbyr.vdm.engines.EngineFactory;
import com.ingbyr.vdm.engines.IEngine;
import com.ingbyr.vdm.engines.IEngineConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
class EngineYoutubedlTest {

    private Path pwd = Paths.get(System.getProperty("user.dir"));
    private Path tmp = pwd.getParent().resolve("tmp");
    private Path enginesDir = pwd.getParent().resolve("native").resolve("engine");
    private Path enginePath = enginesDir.resolve("youtube-dl");
    //    private Path enginePath = enginesDir.resolve("fake");
    private IEngine engine = EngineFactory.getInstance(EngineYoutubedl.class);

    class TestEngineConfig implements IEngineConfig {

        @Override
        public Path engine() {
            return enginePath;
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
    void currentVersion() throws IOException, InterruptedException, ExecutionException {
        engine.setConfig(new TestEngineConfig());
        CompletableFuture p = engine.currentVersion(log::debug);
        p.get();
    }

    @Test
    void fetchMediaInfo() throws IOException, InterruptedException, ExecutionException {
        engine.setConfig(new TestEngineConfig());
        CompletableFuture res = engine.fetchMediaInfo(info -> log.debug(info.toString()));
        res.get();
    }

    @Test
    void downloadMedia() throws IOException, ExecutionException, InterruptedException {
        engine.setConfig(new TestEngineConfig());
        CompletableFuture res = engine.download(downloadStatus -> log.debug("[Test Consumer] {}", downloadStatus));
        res.get();
    }

    /**
     * Use fake engine path to test
     */
    @Test
    void stopEngineTest() throws IOException, InterruptedException, ExecutionException {
        engine.setConfig(new TestEngineConfig());
        CompletableFuture res = engine.currentVersion(log::debug);
        log.debug("Main thread sleep 1s");
        Thread.sleep(1000);
        log.debug("Stop engine");
        engine.stop();
        log.debug("There should no output below except for 'Finished job' msg");
        res.get();
    }
}