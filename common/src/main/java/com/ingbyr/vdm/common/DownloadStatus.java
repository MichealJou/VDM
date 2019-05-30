package com.ingbyr.vdm.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class DownloadStatus {
    public String title;
    public String progress;
    public String speed;
    public String fileSize;
}
