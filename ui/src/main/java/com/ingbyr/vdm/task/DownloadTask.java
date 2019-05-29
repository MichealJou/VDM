package com.ingbyr.vdm.task;

import com.ingbyr.vdm.engines.IEngine;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Data
@Builder
public class DownloadTask {
    private IEngine IEngine;
    private SimpleStringProperty title;
    private SimpleStringProperty size;
    private SimpleDoubleProperty progress;
    private SimpleObjectProperty<LocalDateTime> createdTime;
    private SimpleObjectProperty<DownloadTaskStatus> status;
}
