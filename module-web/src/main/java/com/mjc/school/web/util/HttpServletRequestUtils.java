package com.mjc.school.web.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mjc.school.web.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Slf4j
public class HttpServletRequestUtils {
    private static final ObjectMapper mapper = new JsonMapper().findAndRegisterModules();

    private HttpServletRequestUtils() {}

    public static <T> T readObjectFromRequestBody(
            HttpServletRequest request, Class<T> clazz
    ) throws NotUTFEncodingWebException, NoDataInRequestWebException, IllegalDataFormatWebException {
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new NotUTFEncodingWebException();
        }

        StringBuilder builder = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            log.error("Error reading request body data", e);
            throw new CustomWebRuntimeException();
        }

        if (builder.isEmpty()) {
            throw new NoDataInRequestWebException();
        }

        try {
            return mapper.readValue(builder.toString(), clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalDataFormatWebException(builder.toString());
        }
    }

    public static int getLimitValueFromRequest(HttpServletRequest req) throws IllegalLimitValueWebException {
        String valueAsString = req.getParameter("limit");

        if (valueAsString != null) {
            int valueAsInt;
            try {
                valueAsInt = Integer.parseInt(valueAsString);
            } catch (NumberFormatException e) {
                throw new IllegalLimitValueWebException(valueAsString);
            }

            if (valueAsInt < 0) {
                throw new IllegalLimitValueWebException(valueAsInt);
            }

            return valueAsInt;
        } else {
            return -1;
        }
    }

    public static int getOffsetValueFromRequest(HttpServletRequest req) throws IllegalOffsetValueWebException {
        String valueAsString = Optional.ofNullable(req.getParameter("offset")).orElse("0");

        int valueAsInt;
        try {
            valueAsInt = Integer.parseInt(valueAsString);
        } catch (NumberFormatException e) {
            throw new IllegalOffsetValueWebException(valueAsString);
        }

        if (valueAsInt < 0) {
            throw new IllegalOffsetValueWebException(valueAsInt);
        }

        return valueAsInt;
    }

    public static long getIdFromPath(HttpServletRequest req) throws IllegalNewsIdValueWebException {
        String idAsString = req.getPathInfo().replace("/", "");
        if (idAsString.isEmpty()) {
            throw new IllegalNewsIdValueWebException(idAsString);
        }

        try {
            return Long.parseLong(idAsString);
        } catch (NumberFormatException e) {
            throw new IllegalNewsIdValueWebException(idAsString);
        }
    }
}
