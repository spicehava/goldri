package club.goldri.core.common.config;

import club.goldri.core.constant.AuthConstant;
import com.google.common.base.Predicate;
import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        List<Parameter> params = new ArrayList<Parameter>();

        ParameterBuilder tokenParam1 = new ParameterBuilder();
        tokenParam1.name(AuthConstant.DEFAULT_TOKEN_NAME).description("令牌(" + AuthConstant.TOKEN_HEADER_PREFIX + "开头)").modelRef(new ModelRef("string")).parameterType("header").required(false).build();

        ParameterBuilder tokenParam2 = new ParameterBuilder();
        tokenParam2.name(AuthConstant.CLIENT_PARAM_USERNAME).description("用户名").modelRef(new ModelRef("string")).parameterType("header").required(false).build();

        params.add(tokenParam1.build());
        params.add(tokenParam2.build());

        Predicate<RequestHandler> predicate = new Predicate<RequestHandler>() {
            @Override
            public boolean apply(RequestHandler input) {
                Class<?> declaringClass = input.declaringClass();
                if (declaringClass == BasicErrorController.class)// 排除
                    return false;
                if(declaringClass.isAnnotationPresent(RestController.class)) // 被注解的类
                    return true;
                if(input.isAnnotatedWith(ResponseBody.class)) // 被注解的方法
                    return true;
                return false;
            }
        };
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("web端接口")
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .select()
                .apis(predicate)//也可以直接这样设置RequestHandlerSelectors.basePackage("club.goldri.*.controller")
                .build()
                .globalOperationParameters(params);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("xxx项目接口")//大标题
                .description("所有对外开发的API列表")//描述
                .version("V1.0")//版本
                .build();
    }
}