package club.goldri.core.aspect;

import club.goldri.core.common.exception.NotFoundException;
import club.goldri.core.common.exception.ForbiddenException;
import club.goldri.core.util.ResponseUtil;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *  title: 全局异常处理切面
 *  Description: 利用 @ControllerAdvice + @ExceptionHandler组合处理Controller层RuntimeException异常
 */
@ControllerAdvice
@ResponseBody
public class ExceptionAspect {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAspect.class);

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e) {
        logger.error("could_not_read_json...", e.getMessage());
        return ResponseUtil.failure("JSON 格式错误：" + e.getMessage());
    }

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity handleValidationException(MethodArgumentNotValidException e) {
        logger.error("parameter_validation_exception...", e);
        return ResponseUtil.failure("parameter_validation_exception");
    }

    /**
     * 处理未发现的异常,比如用户名不存在等, 抛出http status 404
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity handleNotFoundException(Exception e) {
        logger.error("request_not_fond...",e.getMessage());
        return ResponseUtil.failure("未找到资源！");
    }

    /**
     * 405 - Method Not Allowed。HttpRequestMethodNotSupportedException
     * 是ServletException的子类,需要Servlet API支持
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        logger.error("request_method_not_supported...", e);
        return ResponseUtil.failure("不被支持的访问方法！");
    }

    /**
     * 415 - Unsupported Media Type。HttpMediaTypeNotSupportedException
     * 是ServletException的子类,需要Servlet API支持
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler({ HttpMediaTypeNotSupportedException.class })
    public ResponseEntity handleHttpMediaTypeNotSupportedException(Exception e) {
        logger.error("content_type_not_supported...", e);
        return ResponseUtil.failure("不支持的媒体类型！");
    }

    /**
     * 403 - Forbidden
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity handleForbiddenException(Exception e) {
        logger.error("request_forbidden...",e.getMessage());
        return ResponseUtil.failure(e.getMessage());
    }

    /**
     * shiro认证异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity handleUnauthenticatedException(Exception e) {
        logger.error("Internal Server Error...", e);
        return ResponseUtil.failure("身份认证失败，请重新登录！");
    }

    /**
     * shiro 授权异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity handleUnauthorizedException(Exception e) {
        logger.error("Internal Server Error...", e);
        return ResponseUtil.failure("无权访问！");
    }

    /**
     * 500 - shiro权限异常处理
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity handleAuthorizationException(Exception e) {
        logger.error("Internal Server Error...", e);
        return ResponseUtil.failure("身份认证失败！");
    }

    /**
     * 500 - Internal Server Error
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e) {
        logger.error("Internal Server Error...", e);
        return ResponseUtil.failure("程序内部错误！");
    }
}
