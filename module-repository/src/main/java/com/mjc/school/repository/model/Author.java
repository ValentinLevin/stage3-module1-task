package com.mjc.school.repository.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
public class Author extends Entity {
    @NotEmpty(message = "Author's name is required")
    @Size(min = 3, message = "The author's name must be at least 3 characters")
    @Size(max = 15, message = "The author's name must be no more than 15 characters")
    private String name;

    public Author() {

    }

    public Author(String name) {
        this.setId(0L);
        this.name = name;
    }

    @JsonCreator()
    public Author(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name
    ) {
        this.setId(id);
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Author a)) {
            return false;
        }

        return Objects.equals(this.getId(), a.getId())
                && Objects.equals(this.getName(), a.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), name);
    }

    @Override
    public Author copy() {
        return new Author(this.getId(), this.getName());
    }
}
