package club.goldri.core.constant;

public class Constant {
    //用户状态：正常
    public static final  String USER_STATE_NORMAL = "NORMAL";
    //用户状态：锁定
    public static final  String USER_STATE_LOCK = "LOCK";
    //删除状态：正常
    public static final  String DEL_FLAG_NORMAL = "NORMAL";
    //删除状态：删除
    public static final  String DEL_FLAG_DELETE = "DELETE";

    //返回头信息
    public static final String HEADER_ERROR = "X-tjmjApp-error";
    public static final String HEADER_ALERT = "X-tjmjApp-alert";
    public static final String HEADER_PARAM = "X-tjmjApp-param";

    public static final String EXPOSE_HEADER = HEADER_ERROR + ", " + HEADER_ALERT + ", " + HEADER_PARAM;

    //阿里短信相关配置
    public static final String SMS_ACCESS_KEY_ID = "LTAIcCgvfg8zE21T";
    public static final String SMS_ACCESS_KEY_SECRET = "J5Ft3US5uXKLl82qjtLY5yX7hBOQ9a";
    //短信模板
    public static final String SMS_TEMPLATE_CODE_TEST = "SMS_119190070";
}
