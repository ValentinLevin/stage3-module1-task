package com.mjc.school.mapper;

import com.mjc.school.dto.EditNewsRequestDTO;
import com.mjc.school.dto.NewsDTO;
import com.mjc.school.model.News;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NewsMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    static {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        Converter<LocalDateTime, String> localDateTimeToStringConverter = ctx -> ctx.getSource() == null ? null : ctx.getSource().format(dateTimeFormatter);
        Converter<String, LocalDateTime> stringToLocalDateTimeConverter = ctx -> ctx.getSource() == null ? null : LocalDateTime.from(dateTimeFormatter.parse(ctx.getSource()));

        modelMapper.addConverter(localDateTimeToStringConverter);
        modelMapper.addConverter(stringToLocalDateTimeConverter);

        modelMapper.typeMap(EditNewsRequestDTO.class, News.class).addMappings(mapper -> mapper.skip(News::setId));
        modelMapper.typeMap(EditNewsRequestDTO.class, News.class).addMappings(mapper -> mapper.skip(News::setCreateDate));
        modelMapper.typeMap(EditNewsRequestDTO.class, News.class).addMappings(mapper -> mapper.skip(News::setLastUpdateDate));
    }

    private NewsMapper() {}

    public static News fromEditNewsRequestDTO(EditNewsRequestDTO dto) {
        return dto == null ? null : modelMapper.map(dto, News.class);
    }

    public static NewsDTO toNewsDTO(News news) {
        return news == null ? null : modelMapper.map(news, NewsDTO.class);
    }
}
