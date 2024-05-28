package com.mjc.school.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mjc.school.service.dto.NewsDTO;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"errorCode", "errorMessage", "items", "start", "size", "totalItemCount"})
public class GetNewsListResponseDTO extends BaseResponseDTO {
    @JsonProperty("items")
    private List<NewsDTO> items;

    @JsonProperty("start")
    private long start;

    @JsonProperty("size")
    private long size;

    @JsonProperty("totalItemCount")
    private long totalItemCount;
}
