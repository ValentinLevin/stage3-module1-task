package com.mjc.school.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mjc.school.service.dto.NewsDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UpdateNewsResponseDTO extends BaseResponseDTO {
    @JsonProperty("data")
    private final NewsDTO data;
}
