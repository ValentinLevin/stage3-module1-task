package com.mjc.school.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class EditNewsRequestDTO {
    @NotEmpty(message = "News title is required")
    @Size(min = 5, message = "The title of the news must be at least 5 characters")
    @Size(max = 30, message = "The title of the news must be no more than 30 characters")
    private String title;

    @NotEmpty(message = "News content is required")
    @Size(min = 5, message = "The content of the news must be at least 5 characters")
    @Size(max = 255, message = "The content of the news must be no more than 255 characters")
    private String content;

    @NotNull(message = "Author id is required")
    @Positive(message = "Author id must be a positive")
    private Long authorId;

    @Override
    public int hashCode() {
        return Objects.hash(title, content, authorId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof EditNewsRequestDTO other)) {
            return false;
        }

        return Objects.equals(title, other.title)
                && Objects.equals(content, other.content)
                && Objects.equals(authorId, other.authorId);
    }

    @JsonCreator
    public EditNewsRequestDTO(
            @JsonProperty("title") String title,
            @JsonProperty("content") String content,
            @JsonProperty("authorId") Long authorId
    ) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
    }
}
