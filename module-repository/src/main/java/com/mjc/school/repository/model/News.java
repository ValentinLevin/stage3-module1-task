package com.mjc.school.repository.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
public class News extends Entity {
    @NotEmpty(message = "News title is required")
    @Size(min = 5, message = "The title of the news must be at least 5 characters")
    @Size(max = 30, message = "The title of the news must be no more than 30 characters")
    private String title;

    @NotEmpty(message = "News content is required")
    @Size(min = 5, message = "The content of the news must be at least 5 characters")
    @Size(max = 255, message = "The content of the news must be no more than 255 characters")
    private String content;

    @NotNull(message = "The date/time the data was created is required.")
    private LocalDateTime createDate;

    @NotNull(message = "The date/time of the last data update is required")
    private LocalDateTime lastUpdateDate;

    @NotNull(message = "Author id is required")
    private Long authorId;

    public News() { }

    @Override
    public News copy() {
        return new News(
                this.getId(),
                this.getTitle(),
                this.getContent(),
                this.getCreateDate(),
                this.getLastUpdateDate(),
                this.getAuthorId()
        );
    }

    @JsonCreator()
    public News(
            @JsonProperty("id") Long id,
            @JsonProperty("title") String title,
            @JsonProperty("content") String content,
            @JsonProperty("createDate") @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss") LocalDateTime createDate,
            @JsonProperty("lastUpdateDate") @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss") LocalDateTime lastUpdateDate,
            @JsonProperty("authorId") Long authorId
    ) {
        this.setId(id);
        this.title = title;
        this.content = content;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
        this.authorId = authorId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.getId(),
                title,
                content,
                createDate,
                lastUpdateDate,
                authorId
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof News other)) {
            return false;
        }

        return Objects.equals(this.getId(), other.getId())
                && Objects.equals(this.getTitle(), other.getTitle())
                && Objects.equals(this.getContent(), other.getContent())
                && Objects.equals(this.getCreateDate(), other.getCreateDate())
                && Objects.equals(this.getLastUpdateDate(), other.getLastUpdateDate())
                && Objects.equals(this.getAuthorId(), other.getAuthorId());
    }
}
