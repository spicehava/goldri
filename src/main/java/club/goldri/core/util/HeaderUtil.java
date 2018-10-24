package club.goldri.core.util;

import club.goldri.core.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.UnsupportedEncodingException;

/**
 * Utility class for HTTP headers creation.
 */
public final class HeaderUtil {

    private static final Logger logger = LoggerFactory.getLogger(HeaderUtil.class);

    private HeaderUtil() {
    }

    /**
     * 在请求头中增加提示信息
     * @param message
     * @return
     */
    public static HttpHeaders createAlert(String message) {
        logger.debug("处理成功请求头："+ message);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        try {
            headers.add(Constant.HEADER_ALERT, java.net.URLEncoder.encode(message,"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return headers;
    }

    /**
     * 在请求头中增加提示信息和参数信息
     * @param message
     * @return
     */
    public static HttpHeaders createAlert(String message, String param) {
        logger.debug("处理成功请求头"+ "提示信息：" + message + ";参数：" + param);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        try {
            headers.add(Constant.HEADER_ALERT, java.net.URLEncoder.encode(message,"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        headers.add(Constant.HEADER_PARAM, param);
        return headers;
    }

    /**
     * 在请求头中增加失败提示信息
     * @param message
     * @return
     */
    public static HttpHeaders createFailureAlert(String message) {
        logger.debug("处理失败请求头："+ message);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        try {
            headers.add(Constant.HEADER_ERROR, java.net.URLEncoder.encode(message,"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return headers;
    }


}
