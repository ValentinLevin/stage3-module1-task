package com.mjc.school.service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NewsDTO {
    @JsonProperty("id")
    private Long id;

    @NotEmpty(message = "News title is required")
    @Size(min = 5, message = "The title of the news must be at least 5 characters")
    @Size(max = 30, message = "The title of the news must be no more than 30 characters")
    @JsonProperty("title")
    private String title;

    @NotEmpty(message = "News content is required")
    @Size(min = 5, message = "The content of the news must be at least 5 characters")
    @Size(max = 30, message = "The content of the news must be no more than 255 characters")
    @JsonProperty("content")
    private String content;

    @JsonProperty("createDate")
    private String createDate;

    @JsonProperty("lastUpdateDate")
    private String lastUpdateDate;

    @JsonProperty("author")
    private AuthorDTO author;

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, createDate, lastUpdateDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NewsDTO other)) {
            return false;
        }

        return Objects.equals(id, other.id)
                && Objects.equals(title, other.title)
                && Objects.equals(content, other.content)
                && Objects.equals(createDate, other.createDate)
                && Objects.equals(lastUpdateDate, other.lastUpdateDate)
                && Objects.equals(author, other.author);
    }

    @JsonCreator
    public NewsDTO(
            @JsonProperty("id") Long id,
            @JsonProperty("title") String title,
            @JsonProperty("content") String content,
            @JsonProperty("author") AuthorDTO author
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
    }
}
