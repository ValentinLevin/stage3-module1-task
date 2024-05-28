package com.mjc.school.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"errorCode", "errorMessage", "data"})
public class GetNewsItemResponseDTO extends BaseResponseDTO {
    @JsonProperty("data")
    private NewsDTO data;
}
