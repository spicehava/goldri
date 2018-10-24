package club.goldri.web.system.domain;

import club.goldri.core.util.StringUtil;
import club.goldri.web.system.domain.enums.LogType;
import club.goldri.core.common.domain.BaseDomain;

import javax.persistence.*;
import java.util.Map;

@Table(name = "sys_log")
public class SysLog extends BaseDomain {
    /**
     * 日志类型。数据字典：LogType
     */
    private LogType type;

    /**
     * 日志操作描述：记录操作描述信息
     */
    private String description;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 用户代理：记录用户所用浏览器信息
     */
    private String browser;

    /**
     * 请求资源地址：请求的url
     */
    private String uri;

    /**
     * 请求方法：包括GET、POST等方法
     */
    private String method;

    /**
     * 创建人姓名：记录姓名，便于页面显示，提高查询效率
     */
    private String creater;

    /**
     * 参数
     */
    private String params;

    /**
     * 异常信息
     */
    private String exception;

    /**
     * 获取日志类型。数据字典：LogType
     *
     * @return type - 日志类型。数据字典：LogType
     */
    public LogType getType() {
        return type;
    }

    /**
     * 设置日志类型。数据字典：LogType
     *
     * @param type 日志类型。数据字典：LogType
     */
    public void setType(LogType type) {
        this.type = type;
    }

    /**
     * 获取日志操作描述：记录操作描述信息
     *
     * @return description - 日志操作描述：记录操作描述信息
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置日志操作描述：记录操作描述信息
     *
     * @param description 日志操作描述：记录操作描述信息
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取ip地址
     *
     * @return ip - ip地址
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置ip地址
     *
     * @param ip ip地址
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 获取用户代理：记录用户所用浏览器信息
     *
     * @return browser - 用户代理：记录用户所用浏览器信息
     */
    public String getBrowser() {
        return browser;
    }

    /**
     * 设置用户代理：记录用户所用浏览器信息
     *
     * @param browser 用户代理：记录用户所用浏览器信息
     */
    public void setBrowser(String browser) {
        this.browser = browser;
    }

    /**
     * 获取请求资源地址：请求的url
     *
     * @return uri - 请求资源地址：请求的url
     */
    public String getUri() {
        return uri;
    }

    /**
     * 设置请求资源地址：请求的url
     *
     * @param uri 请求资源地址：请求的url
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * 获取请求方法：包括GET、POST等方法
     *
     * @return method - 请求方法：包括GET、POST等方法
     */
    public String getMethod() {
        return method;
    }

    /**
     * 设置请求方法：包括GET、POST等方法
     *
     * @param method 请求方法：包括GET、POST等方法
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * 获取创建人姓名：记录姓名，便于页面显示，提高查询效率
     *
     * @return creater - 创建人姓名：记录姓名，便于页面显示，提高查询效率
     */
    public String getCreater() {
        return creater;
    }

    /**
     * 设置创建人姓名：记录姓名，便于页面显示，提高查询效率
     *
     * @param creater 创建人姓名：记录姓名，便于页面显示，提高查询效率
     */
    public void setCreater(String creater) {
        this.creater = creater;
    }

    /**
     * 获取参数
     *
     * @return params - 参数
     */
    public String getParams() {
        return params;
    }

    /**
     * 设置参数
     *
     * @param params 参数
     */
    public void setParams(String params) {
        this.params = params;
    }

    public void setParams(Map paramMap){
        if (paramMap == null){
            return;
        }
        StringBuilder params = new StringBuilder();
        for (Map.Entry<String, String[]> param : ((Map<String, String[]>)paramMap).entrySet()){
            params.append(("".equals(params.toString()) ? "" : "&") + param.getKey() + "=");
            String paramValue = (param.getValue() != null && param.getValue().length > 0 ? param.getValue()[0] : "");
            params.append(StringUtil.abbr(StringUtil.endsWithIgnoreCase(param.getKey(), "password") ? "" : paramValue, 100));
        }
        this.params = params.toString();
    }

    /**
     * 获取异常信息
     *
     * @return exception - 异常信息
     */
    public String getException() {
        return exception;
    }

    /**
     * 设置异常信息
     *
     * @param exception 异常信息
     */
    public void setException(String exception) {
        this.exception = exception;
    }
}