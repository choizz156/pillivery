package com.team33.modulecore.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

public class JsonMapper {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonMapper(){}
    public static <T> T stringToObj(HttpServletRequest request, T object) throws IOException {
         return OBJECT_MAPPER.readValue(request.getInputStream(), (Class<T>) object.getClass());
    }
    public static <T> String objToString(T object){
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
