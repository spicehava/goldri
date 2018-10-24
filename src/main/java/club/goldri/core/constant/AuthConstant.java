package club.goldri.core.constant;

/**
 * 验证相关常量
 */
public class AuthConstant {
    //用于验证使用：Header中的名字
    public static  final String DEFAULT_TOKEN_NAME = "Authorization";

    //约定已什么信息开头
    public static  final String TOKEN_HEADER_PREFIX = "Bearer";

    //私有秘钥secretKey（可以对其base64加密）
    public static final String SECRETKET = "eHh4eGJiYmNjY2RkZGVlZWZmZmRkZA==";

    //12小时
    public static Long TTLMILLS = 43200000L;

    //验证身份标识
    public static final String CLIENT_PARAM_USERNAME = "username";

    //用户的id
    public static final String CLIENT_PARAM_USERID = "userId";
}
