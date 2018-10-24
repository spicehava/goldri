package club.goldri.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;

public class TestUtil {

    public static HttpHeaders getHeaders(String Authorization, String username){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", Authorization);
        headers.add("username", username);
        return headers;
    }

    public static String writeValueAsString(Object object){
        ObjectMapper mapper = new ObjectMapper();
        String result;
        try {
            result = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "";
        }
        return result;
    }
}
