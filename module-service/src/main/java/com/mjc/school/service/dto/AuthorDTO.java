package com.mjc.school.service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AuthorDTO {
    @NotNull(message = "Author id is required")
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof AuthorDTO a)) {
            return false;
        }

        return Objects.equals(id, a.id) && Objects.equals(name, a.name);
    }

    @JsonCreator
    public AuthorDTO(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name
    ) {
        this.id = id;
        this.name = name;
    }
}
