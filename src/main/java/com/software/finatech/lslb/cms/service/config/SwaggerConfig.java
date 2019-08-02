package com.software.finatech.lslb.cms.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {//extends WebMvcConfigurationSupport{
   /* @Bean
    public Docket healthPayApi() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("HealthPay-API")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.software.finatech.healthpay.controllers"))
                .paths(PathSelectors.any())
                .build();
    }*/

   /* public BoonHttpMessageConverter boonHttpMessageConverter() {
        BoonHttpMessageConverter jsonConverter = new BoonHttpMessageConverter();
        return jsonConverter;
    }

    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> messageConverters) { ;
        messageConverters.add(boonHttpMessageConverter());
    }*/

    @Bean
    public Docket lslbCmsServiceApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.software.finatech.lslb.cms.service.controller"))
                //.paths(PathSelectors.regex("/api.*"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(metaData());
    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .title("LSLB CMS Service API")
                //.description("\"REST API for HealthPay\"")
                .version("1.0.0")
                .build();
    }
  /*  @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }*/
}
