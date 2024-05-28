package com.mjc.school.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Model {
    private Long id;

    protected Model() {}

    @JsonCreator()
    protected Model(
            @JsonProperty("id") Long id
    ) {
        this.id = id;
    }

    public abstract <T extends Model> T copy();
}
