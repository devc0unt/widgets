package com.miro.dev.widgets.model;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Limit {
    private final Integer MAX = 500;
    private final Integer DEFAULT = 10;
    private final Integer limit;
    private Integer offset = 0;

    public Limit(Integer limit, Integer offset) {
        if(Objects.isNull(limit) || limit <= 0){
            this.limit = DEFAULT;
        } else if(limit > MAX) {
            this.limit = MAX;
        } else {
            this.limit = limit;
        }
        if(!Objects.isNull(offset) && offset > 0){
            this.offset = offset;
        }
    }

    public static Limit defaultLimit() {
        return new Limit(10, 0);
    }
}
