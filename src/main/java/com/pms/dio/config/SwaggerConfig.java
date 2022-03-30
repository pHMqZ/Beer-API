package com.pms.dio.config;

import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurationSupport{
	
	private static final String BASE_PACKAGE = "com.pms.dio.controller";
	private static final String API_TITLE = "API de Estoque cervejeiro";
	private static final String API_DESCRIPTION = "REST API controle de estoque de cervejas";
	private static final String CONTACT_NAME = "Phillip Marques";
	private static final String CONTACT_GITHUB = "https://gtihub.com/pHMqZ";
	private static final String BASE_API = "Baseado na API da DIO de gerenciamento de estoque de cerveja com testes unitários - Rodrigo Peleias";

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(basePackage(BASE_PACKAGE))
				.paths(PathSelectors.any())
				.build()
				.apiInfo(buildApiInfo());
	}

	private ApiInfo buildApiInfo() {
		return new ApiInfoBuilder()
				.title(API_TITLE)
				.description(API_DESCRIPTION)
				.version("1.0.0")
				.contact(new Contact(CONTACT_NAME, CONTACT_GITHUB, BASE_API))
				.build();
	}
	
	@Override
	  protected void addResourceHandlers(ResourceHandlerRegistry registry) {
	    registry.addResourceHandler("swagger-ui.html")
	        .addResourceLocations("classpath:/META-INF/resources/");

	    registry.addResourceHandler("/webjars/**")
	        .addResourceLocations("classpath:/META-INF/resources/webjars/");
	  }
}
