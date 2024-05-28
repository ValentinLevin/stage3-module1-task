package com.mjc.school.mapper;

import com.mjc.school.dto.EditNewsRequestDTO;
import com.mjc.school.dto.NewsDTO;
import com.mjc.school.model.NewsModel;
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

        modelMapper.typeMap(EditNewsRequestDTO.class, NewsModel.class).addMappings(mapper -> mapper.skip(NewsModel::setId));
        modelMapper.typeMap(EditNewsRequestDTO.class, NewsModel.class).addMappings(mapper -> mapper.skip(NewsModel::setCreateDate));
        modelMapper.typeMap(EditNewsRequestDTO.class, NewsModel.class).addMappings(mapper -> mapper.skip(NewsModel::setLastUpdateDate));
    }

    private NewsMapper() {}

    public static NewsModel fromEditNewsRequestDTO(EditNewsRequestDTO dto) {
        return dto == null ? null : modelMapper.map(dto, NewsModel.class);
    }

    public static NewsDTO toNewsDTO(NewsModel newsModel) {
        return newsModel == null ? null : modelMapper.map(newsModel, NewsDTO.class);
    }
}
