package com.ingbyr.vdm.engines.external;

import com.ingbyr.vdm.common.Cookie;
import com.ingbyr.vdm.common.DownloadStatus;
import com.ingbyr.vdm.common.DownloadType;
import com.ingbyr.vdm.common.Proxy;
import com.ingbyr.vdm.engines.EngineFactory;
import com.ingbyr.vdm.engines.IEngine;
import com.ingbyr.vdm.engines.IEngineConfig;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
        CompletableFuture<Process> p = engine.currentVersion(System.out::println);
        p.get();
    }

    @Test
    void fetchMediaInfo() throws IOException, InterruptedException, ExecutionException {
        engine.setConfig(new TestEngineConfig());
        CompletableFuture res = engine.fetchMediaInfo(System.out::println);
        res.get();
    }

    @Test
    void downloadMedia() {
        engine.setConfig(new TestEngineConfig());
        BlockingQueue<DownloadStatus> q = engine.getDownloadStatusQueue();
        new Thread(() -> {
            try {
                engine.download();
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();

        while (engine.isRunning()) {
            if (!q.isEmpty()) System.out.println(q.poll());
        }
    }
}