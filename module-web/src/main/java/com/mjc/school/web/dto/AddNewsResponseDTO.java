package com.mjc.school.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mjc.school.service.dto.NewsDTO;
import com.mjc.school.web.constant.RESULT_CODE;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({"errorCode", "errorMessage", "data"})
public class AddNewsResponseDTO extends BaseResponseDTO {
    @JsonProperty("data")
    private NewsDTO data;
    public AddNewsResponseDTO(NewsDTO data) {
        super(RESULT_CODE.SUCCESS.getErrorCode());
        this.data = data;
    }
}
