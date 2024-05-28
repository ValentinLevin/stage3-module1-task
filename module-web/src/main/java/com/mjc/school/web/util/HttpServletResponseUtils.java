package com.mjc.school.web.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;

@Slf4j
public class HttpServletResponseUtils {
    private static final ObjectMapper mapper = new JsonMapper().findAndRegisterModules();

    private HttpServletResponseUtils() {}

    public static <T> void writePayloadIntoResponseBody(HttpServletResponse response, T value, int status) {
        try {
            String valueAsString = mapper.writeValueAsString(value);

            response.setStatus(status);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            try (PrintWriter writer = response.getWriter()) {
                writer.write(valueAsString);
                writer.flush();
            }
        } catch (Exception e) {
            log.error("Error when writing the result of request processing in response", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
