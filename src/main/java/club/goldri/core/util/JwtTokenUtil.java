package club.goldri.core.util;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import javax.xml.bind.DatatypeConverter;

/**
 * 使用jwt鉴权
 */
public class JwtTokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    /**
     * 生成秘钥
     * @param username 登录的用户名
     * @param ttlMills 存活时间（毫秒）
     * @param secretKey 私有秘钥-base64加密
     * @return
     */
    public static String createToken(String username, long ttlMills, String secretKey) {

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //生成jwtBuilder并构造参数信息
        JwtBuilder builder = Jwts.builder()
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS256, secretKey);


        //添加Token过期时间
        if (ttlMills >= 0) {
            long expMillis = nowMillis + ttlMills;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp).setNotBefore(now);
        }

        //生成Jwt Token
        return builder.compact();
    }

    /**
     * 根据秘钥解析token
     * @param token
     * @param secretKey
     * @return
     */
    public static Claims parseToken(String token, String secretKey){
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                    .parseClaimsJws(token).getBody();
            return claims;
        } catch (Exception ex){
            return null;
        }
    }

    /**
     * 身份验证
     * @param authToken
     * @return
     */
    public static boolean validateToken(String authToken, String secretKey) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.info("Invalid JWT signature.");
            logger.trace("Invalid JWT signature trace: {}", e);
        } catch (MalformedJwtException e) {
            logger.info("Invalid JWT token.");
            logger.trace("Invalid JWT token trace: {}", e);
        } catch (ExpiredJwtException e) {
            logger.info("Expired JWT token.");
            logger.trace("Expired JWT token trace: {}", e);
        } catch (UnsupportedJwtException e) {
            logger.info("Unsupported JWT token.");
            logger.trace("Unsupported JWT token trace: {}", e);
        } catch (IllegalArgumentException e) {
            logger.info("JWT token compact of handler are invalid.");
            logger.trace("JWT token compact of handler are invalid trace: {}", e);
        }
        return false;
    }
}
