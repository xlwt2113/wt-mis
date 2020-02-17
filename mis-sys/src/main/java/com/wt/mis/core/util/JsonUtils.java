package com.wt.mis.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JsonUtils {

    private static ObjectMapper objectMapper;

    @Autowired
    private JsonUtils(ObjectMapper om) {
        objectMapper = om;
    }

    public static <T> T readValue(String string, Class<T> clazz) {
        try {
            return objectMapper.readValue(string, clazz);
        } catch (IOException e) {
            log.warn("read to Object error", e);
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * 对象转换成json
     *
     * @param obj
     * @return
     */
    public static String objectToJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to Json error", e);
            throw new UnsupportedOperationException(e);
        }
    }


}
