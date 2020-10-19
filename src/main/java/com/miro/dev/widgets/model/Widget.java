package com.miro.dev.widgets.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Widget {
    private Long id;
    @JsonProperty("x")
    private Integer xIndex;
    @JsonProperty("y")
    private Integer yIndex;
    @JsonProperty("z")
    private Integer zIndex;
    private Integer width;
    private Integer height;
    private LocalDateTime modifiedAt;

    public void incrementZIndex() { zIndex++; }
}
