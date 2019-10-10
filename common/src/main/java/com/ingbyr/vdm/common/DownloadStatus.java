package com.ingbyr.vdm.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DownloadStatus implements Cloneable {
    public String title;
    public String progress;
    public String speed;
    public String fileSize;

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
