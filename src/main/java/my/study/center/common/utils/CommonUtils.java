package my.study.center.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

/**
 * common utils method implements
 *
 * @author minssogi
 */
public class CommonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * convert string to class
     *
     * @param strValue String
     * @param clz Class
     * @return Optional<T>
     */
    public static <T> Optional<T> stringToObject(String strValue, Class<T> clz) {
        try {
            return Optional.ofNullable(objectMapper.readValue(strValue, clz));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static String objectToString(Object obj) {

        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return "";
    }
}
