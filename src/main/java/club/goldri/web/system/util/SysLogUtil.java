package club.goldri.web.system.util;

import club.goldri.core.util.*;
import club.goldri.web.system.domain.SysLog;
import club.goldri.web.system.domain.SysUser;
import club.goldri.web.system.domain.enums.LogType;
import club.goldri.web.system.mapper.SysLogMapper;
import club.goldri.web.system.service.ResourceService;
import club.goldri.core.util.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 系统日志工具
 */
public class SysLogUtil {
	
	private static SysLogMapper sysLogMapper = SpringUtil.getBean(SysLogMapper.class);

	private static ResourceService resourceService = SpringUtil.getBean(ResourceService.class);

	/**
	 * 保存日志
	 */
	public static void saveLog(HttpServletRequest request, SysUser user, String errorMsg, String description){
		Exception ex = null;
		if(StringUtil.isNotEmpty(errorMsg)){
			ex = new Exception(errorMsg);
		}
		saveLog(request, user, null, ex, description);
	}

	public static void saveLog(HttpServletRequest request, SysUser user, String description){
		saveLog(request, user, null, null, description);
	}
	
	/**
	 * 保存日志
	 */
	public static void saveLog(HttpServletRequest request, SysUser user, Object handler, Exception ex, String description){
		SysLog log = new SysLog();
		log.setDescription(description);
		log.setType(ex == null ? LogType.NORMAL : LogType.EXCEPTION);
		log.setIp(StringUtil.getRemoteAddr(request));
		log.setBrowser(request.getHeader("user-agent"));
		log.setUri(request.getRequestURI());
		log.setParams(request.getParameterMap());
		log.setMethod(request.getMethod());

		BeanUtil.setCreateUser(user, log);
		BeanUtil.setUpdateUser(user, log);
		// 异步保存日志
		new SaveSysLogThread(log, handler, ex).start();
	}
	/**
	 * 保存日志线程
	 */
	public static class SaveSysLogThread extends Thread{
		
		private SysLog sysLog;
		private Object handler;
		private Exception ex;
		
		public SaveSysLogThread(SysLog SysLog, Object handler, Exception ex){
			super(SaveSysLogThread.class.getSimpleName());
			this.sysLog = SysLog;
			this.handler = handler;
			this.ex = ex;
		}
		
		@Override
		public void run() {
			// 获取日志标题
			if (StringUtil.isBlank(sysLog.getDescription())){
				String permission = "";
				if (handler instanceof HandlerMethod){
					Method m = ((HandlerMethod)handler).getMethod();
					RequiresPermissions rp = m.getAnnotation(RequiresPermissions.class);
					permission = (rp != null ? StringUtil.join(rp.value(), ",") : "");
				}
				sysLog.setDescription(getSysResourceNamePath(sysLog.getUri(), permission));
			}
			// 如果有异常，设置异常信息
			sysLog.setException(ExceptionUtil.getStackTraceAsString(ex));
			// 如果无标题并无异常日志，则不保存信息
			if (StringUtil.isBlank(sysLog.getDescription()) && StringUtil.isBlank(sysLog.getException())){
				return;
			}
			// 保存日志信息
			sysLogMapper.insert(sysLog);
		}
	}

	/**
	 * 获取菜单名称路径（如：系统管理-用户管理-添加）
	 */
	public static String getSysResourceNamePath(String href, String permission){
		Map<String, String> menuMap = (Map<String, String>) CacheUtil.get(SystemCacheUtil.CACHE_RESOURCE, SystemCacheUtil.SYS_MENU_MAP);
		if (menuMap == null){
			//获取系统所有菜单
			menuMap = resourceService.listAllMenuTree();
		}
		String menuNamePath = menuMap.get(href);
		if (menuNamePath == null){
			for (String p : StringUtil.split(permission)){
				menuNamePath = menuMap.get(p);
				if (StringUtil.isNotBlank(menuNamePath)){
					break;
				}
			}
			if (menuNamePath == null){
				return "";
			}
		}
		return menuNamePath;
	}
}
