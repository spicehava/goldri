package club.goldri.core.common.config;


import club.goldri.core.shiro.StatelessRealm;
import club.goldri.core.filter.StatelessAuthcFilter;
import club.goldri.core.shiro.StatelessDefaultSubjectFactory;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    private final static Logger logger = LoggerFactory.getLogger(ShiroConfig.class);

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        //设置拦截器
        Map<String, Filter> filters = new LinkedHashMap<String, Filter>();

        //无状态过滤
        filters.put("statelessAuthc", statelessAuthc());
        shiroFilterFactoryBean.setFilters(filters);

        //配置访问权限  拦截策略以键值对存入map
        Map<String, String> filterChainDefinitions = new LinkedHashMap<>();//必须LinkedHashMap
        filterChainDefinitions.put("/**",  "statelessAuthc");//拦截自定义设置


        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitions);

        return shiroFilterFactoryBean;
    }

    @Bean(name = "securityManager")
    public SecurityManager securityManager(){
        DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();

        //设置不创建session
        securityManager.setSubjectFactory(subjectFactory());

        //设置sessionManager禁用掉会话调度器
        securityManager.setSessionManager(sessionManager());

        //设置realm.
        securityManager.setRealm(statelessRealm());

        //注入缓存管理器;
//        securityManager.setCacheManager(ehCacheManager());//这个如果执行多次，也是同样的一个对象;

        //无状态需要设置不创建session，禁用使用Sessions 作为存储策略的实现，但它没有完全地禁用Sessions，所以需要配合context.setSessionCreationEnabled(false);
        ((DefaultSessionStorageEvaluator)((DefaultSubjectDAO)securityManager.getSubjectDAO()).getSessionStorageEvaluator()).setSessionStorageEnabled(false);

        return securityManager;
    }

    /**
     * subject工厂管理器.
     * 用于禁用session
     * @return
     */
    @Bean
    public DefaultWebSubjectFactory subjectFactory(){
        StatelessDefaultSubjectFactory subjectFactory = new StatelessDefaultSubjectFactory();
        return subjectFactory;
    }

    /**
     * session管理器
     * sessionManager通过sessionValidationSchedulerEnabled禁用掉会话调度器，
     * 因为我们禁用掉了会话，所以没必要再定期过期会话了。
     * @return
     */
    @Bean
    public DefaultSessionManager sessionManager(){
        DefaultSessionManager sessionManager = new DefaultSessionManager();
        sessionManager.setSessionValidationSchedulerEnabled(false);
        return sessionManager;
    }

    /**
     * 自定义realm实现
     * @return
     */
    @Bean
    public StatelessRealm statelessRealm(){
        StatelessRealm statelessRealm = new StatelessRealm();
        statelessRealm.setCachingEnabled(false);
        return statelessRealm;
    }

    /**
     * 无状态权限验证
     * @return
     */
    @Bean
    public StatelessAuthcFilter statelessAuthc(){
        StatelessAuthcFilter statelessAuthc = new StatelessAuthcFilter();
        return statelessAuthc;
    }
    /**
     * shiro缓存管理器;
     * 需要注入对应的其它的实体类中：
     * 安全管理器：securityManager
     * @return
     */

//    @Bean(name = "shiroCacheManager")
//    public EhCacheManager ehCacheManager(){
//        logger.info("shiro注入缓存..." + getClass().getName());
//        EhCacheManager cacheManager = new EhCacheManager();
//        cacheManager.setCacheManager(ehCacheManagerFactoryBean().getObject());
//        return cacheManager;
//    }
//
//    @Bean(name = "cacheManager")
//    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean(){
//        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
//        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//
//        ehCacheManagerFactoryBean.setConfigLocation(resolver.getResource("classpath:config/ehcache.xml"));
//        return ehCacheManagerFactoryBean;
//    }
    /**
     *  开启shiro aop注解支持.
     *  使用代理方式;所以需要开启代码支持;
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        LifecycleBeanPostProcessor lifecycleBeanPostProcessor = new LifecycleBeanPostProcessor();
        return lifecycleBeanPostProcessor;
    }

    /**
     * 必须加否则shiro注解无法使用
     * @return
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }
}
