package com.mjc.school.repository.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Entity {
    private Long id;

    protected Entity() {}

    @JsonCreator()
    protected Entity(
            @JsonProperty("id") Long id
    ) {
        this.id = id;
    }

    public abstract <T extends Entity> T copy();
}
