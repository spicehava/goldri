package club.goldri.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Json处理工具类
 * @author xuzhiyuan
 */
public class JSONHelper {
    private static ObjectMapper mapper = new  ObjectMapper();
    /**
     * 将json对象转换为LinkedHashMap
     * @param jsonStr
     * @return
     */
    public static Map toLinkedHashMap(String jsonStr) {

        Map<String, String> data = null;
        try {
            data = mapper.readValue(jsonStr, new TypeReference<LinkedHashMap<String, String>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 序列化对象
     * @param object
     * @return
     */
    public static String bean2json(Object object) {
        String data = "";
        try {
            data = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return data;
    }

}
