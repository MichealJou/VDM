package com.ingbyr.vdm.engines.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingbyr.vdm.common.Proxy;
import com.ingbyr.vdm.engines.AbstractEngine;
import com.ingbyr.vdm.engines.EngineType;
import com.ingbyr.vdm.engines.MediaFormat;
import com.ingbyr.vdm.engines.MediaInfo;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
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
     * Download media output string parser
     */
    private class Parser {
        private final Pattern titlePattern = Pattern.compile("[/\\\\][^/^\\\\]+\\.\\w+");
        private final Pattern progressPattern = Pattern.compile("\\d+\\W?\\d*%");
        private final Pattern speedPattern = Pattern.compile("\\d+\\W?\\d*\\w+/s");
        private final Pattern fileSizePattern = Pattern.compile("\\s\\d+\\W?\\d*\\w+B\\s");

        private Consumer<String> consumer = (str) -> {
            status.title = find(titlePattern, str, status.title);
            status.progress = find(progressPattern, str, status.progress);
            status.speed = find(speedPattern, str, status.speed);
            status.fileSize = find(fileSizePattern, str, status.fileSize);
            log.debug("{}", status);
        };

        private String find(Pattern pattern, String str, String defaultStr) {
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                return matcher.group(0);
            } else return defaultStr;
        }
    }

    public EngineYoutubedl() {
        this.engineType = EngineType.YOUTUBE_DL;
        this.parseDownloadOutput = new Parser().consumer;
    }

    private MediaInfo parseMediaInfo(String strInfo) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readerFor(YoutubedlMediaInfo.class).readValue(strInfo);
    }

    @Override
    public MediaInfo fetchMediaInfo() throws IOException, InterruptedException {
        parseConfig();
        args.put("MediaInfo", "-j");
        args.put("MediaUrl", config.mediaUrl());
        return parseMediaInfo(execAndGetOutput());
    }

    private void prepareDownload() {
        parseConfig();
        args.put("url", config.mediaUrl());
        args.put("-o", Paths.get(config.storagePath(), nameTemplate).normalize().toString());
    }

    @Override
    public void download() throws IOException, InterruptedException {
        prepareDownload();
        exec(parseDownloadOutput);
    }

    @Override
    public void download(String formatID) throws IOException, InterruptedException {
        prepareDownload();
        if (!formatID.isBlank()) {
            args.put("-f", formatID);
        }
        exec(parseDownloadOutput);
    }

    @Override
    public void downloadPlaylist() throws IOException, InterruptedException {
        // TODO youtube-dl download playlist
    }

    @Override
    public String currentVersion() throws IOException, InterruptedException {
        args.clear();
        args.put("version", "--version");
        return execAndGetOutput();
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
