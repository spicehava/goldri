package club.goldri.core.common.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;
import java.util.Properties;

@Configuration
@AutoConfigureAfter(MybatisConfig.class)
public class TkMapperConfig {
    //mappers（dao)的类路径
    private String mapperBasePackage ="club.goldri.web.**.domain";
    //通用Mapper的接口类
    private String mappers = "club.goldri.core.common.mapper.BaseMapper";
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(){
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
        mapperScannerConfigurer.setBasePackage(mapperBasePackage);

        Properties properties = new Properties();
        //通用mapper位置，不要和其他mapper、dao放在同一个目录
        properties.setProperty("mappers", mappers);
        properties.setProperty("notEmpty", "false");
        properties.setProperty("IDENTITY","SELECT REPLACE(UUID(),''-'','''')");
        //主键UUID回写方法执行顺序,默认AFTER,可选值为(BEFORE|AFTER)
        properties.setProperty("ORDER","BEFORE");
        mapperScannerConfigurer.setProperties(properties);
        return mapperScannerConfigurer;
    }

}