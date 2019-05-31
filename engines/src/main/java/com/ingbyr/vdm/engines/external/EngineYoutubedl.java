package com.ingbyr.vdm.engines.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingbyr.vdm.common.DownloadStatus;
import com.ingbyr.vdm.common.Proxy;
import com.ingbyr.vdm.engines.*;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class EngineYoutubedl extends AbstractEngine {

    // youtube-dl output filename setting
    private static final String nameTemplate = "%(title)s.%(ext)s";

    /**
     * Media info model parsed by jackson
     */
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class YoutubedlMediaInfo implements MediaInfo {
        @JsonProperty("fulltitle")
        private String title;

        @JsonProperty("formats")
        private List<YoutubedlMediaFormat> formats;

        @Override
        public String title() {
            return title;
        }

        @Override
        public List<MediaFormat> mediaFormats() {
            return formats.stream().map(format -> (MediaFormat) format).collect(Collectors.toList());
        }

    }

    /**
     * Media format used by Media info
     */
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class YoutubedlMediaFormat implements MediaFormat {
        @JsonProperty("format_id")
        private String formatID;

        @JsonProperty("format")
        private String format;

        @JsonProperty("ext")
        private String extension;

        @JsonProperty("fileSize")
        private String fileSize;

        @JsonProperty("url")
        private String realUrl;

        @Override
        public String formatID() {
            return formatID;
        }

        @Override
        public String format() {
            return format;
        }

        @Override
        public String extension() {
            return extension;
        }

        @Override
        public String fileSize() {
            return fileSize;
        }

        @Override
        public String realUrl() {
            return realUrl;
        }
    }

    /**
     * Download media output string outputConsumer
     */
    private class OutputConsumer implements IEngineOutputConsumer {
        private final Pattern titlePattern = Pattern.compile("[/\\\\][^/^\\\\]+\\.\\w+");
        private final Pattern progressPattern = Pattern.compile("\\d+\\W?\\d*%");
        private final Pattern speedPattern = Pattern.compile("\\d+\\W?\\d*\\w+/s");
        private final Pattern fileSizePattern = Pattern.compile("\\s\\d+\\W?\\d*\\w+B\\s");

        private Consumer<DownloadStatus> downloadStatusConsumer;

        private Consumer<String> downloadOutputConsumer = str -> {
            status.title = find(titlePattern, str, status.title).replace(File.separator, "");
            status.progress = find(progressPattern, str, status.progress);
            status.speed = find(speedPattern, str, status.speed);
            status.fileSize = find(fileSizePattern, str, status.fileSize);
            log.debug("{}", status);
            downloadStatusConsumer.accept(status);
        };

        private StringBuilder output;

        private String find(Pattern pattern, String str, String defaultStr) {
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                return matcher.group(0);
            } else return defaultStr;
        }

        @Override
        public void setDownloadStatusConsumer(Consumer<DownloadStatus> downloadStatusConsumer) {
            this.downloadStatusConsumer = downloadStatusConsumer;
        }

        @Override
        public Consumer<String> getDownloadOutputConsumer() {
            return downloadOutputConsumer;
        }

        @Override
        public Consumer<String> getGeneralOutputConsumer() {
            output = new StringBuilder();
            return output::append;
        }

        @Override
        public String getOutput() {
            if (output == null) {
                throw new IllegalStateException("Must invoke getGeneralOutputConsumer() first");
            } else return output.toString();
        }
    }

    public EngineYoutubedl() {
        this.engineType = EngineType.YOUTUBE_DL;
        this.outputConsumer = new OutputConsumer();
    }

    private MediaInfo parseMediaInfo(String strInfo) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readerFor(YoutubedlMediaInfo.class).readValue(strInfo);
    }

    @Override
    public CompletableFuture<Void> fetchMediaInfo(Consumer<MediaInfo> consumer) throws IOException {
        parseConfig();
        args.put("MediaInfo", "-j");
        args.put("MediaUrl", config.mediaUrl());
        CompletableFuture<Void> res = exec(outputConsumer.getGeneralOutputConsumer());
        return res.thenAccept(p -> {
            try {
                MediaInfo mediaInfo = parseMediaInfo(outputConsumer.getOutput());
                consumer.accept(mediaInfo);
            } catch (IOException e) {
                log.error("Parsing below media info json failed");
                log.error(outputConsumer.getOutput());
                log.error(e.toString());
            }
        });
    }

    private void prepareDownload() {
        parseConfig();
        args.put("url", config.mediaUrl());
        args.put("-o", Paths.get(config.storagePath(), nameTemplate).normalize().toString());
    }

    @Override
    public CompletableFuture<Void> download(Consumer<DownloadStatus> downloadStatusConsumer) throws IOException {
        prepareDownload();
        outputConsumer.setDownloadStatusConsumer(downloadStatusConsumer);
        return exec(outputConsumer.getDownloadOutputConsumer());
    }

    @Override
    public CompletableFuture<Void> download(Consumer<DownloadStatus> downloadStatusConsumer, String formatID) throws IOException {
        prepareDownload();
        if (!formatID.isBlank()) {
            args.put("-f", formatID);
        }
        outputConsumer.setDownloadStatusConsumer(downloadStatusConsumer);
        return exec(outputConsumer.getDownloadOutputConsumer());
    }

    @Override
    public void downloadPlaylist() throws IOException, InterruptedException {
        // TODO youtube-dl download playlist
    }

    @Override
    public CompletableFuture<Void> currentVersion(Consumer<String> consumer) throws IOException {
        args.clear();
        args.put("version", "--version");
        return exec(consumer);
    }

    private void addProxy(Proxy proxy) {
        log.debug("Parse engine config {}", proxy);
        switch (proxy.type) {
            case SOCKS5:
                args.put("--proxy", String.format("socks5://%s:%s", proxy.ip, proxy.port));
                break;
            case HTTP:
                args.put("--proxy", String.format("%s:%s", proxy.ip, proxy.port));
                break;
        }
    }

    @Override
    protected void parseConfig() {
        args.clear();
        config.proxy().ifPresent(this::addProxy);
    }

}
