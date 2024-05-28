package com.mjc.school.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mjc.school.service.dto.NewsDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"errorCode", "errorMessage", "data"})
public class GetNewsItemResponseDTO extends BaseResponseDTO {
    @JsonProperty("data")
    private NewsDTO data;
}
