package club.goldri.core.common.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
public class MybatisConfig {
    @Resource
    private DataSource dataSource;

    private static String MAPPER_PATH = "mapper/*/*.xml";

    private String typeAliasPackage = "club.goldri.web.**.domain";
    @Bean
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        sqlSessionFactory.setTypeAliasesPackage(typeAliasPackage);

//        //分页插件,spring boot 不需要注入，启动时starter会自动注入
//        PageHelper pageHelper = new PageHelper();
//        Properties properties = new Properties();
//        properties.setProperty("reasonable", "true");
//        properties.setProperty("supportMethodsArguments", "true");
//        properties.setProperty("returnPageInfo", "check");
//        properties.setProperty("params", "count=countSql");
//        pageHelper.setProperties(properties);

        //添加插件
        //sqlSessionFactory.setPlugins(new Interceptor[]{pageHelper});

        //添加Mapper扫描目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String mapperPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + MAPPER_PATH;
        try {
            sqlSessionFactory.setMapperLocations(resolver.getResources(mapperPath));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return sqlSessionFactory.getObject();
    }
}
