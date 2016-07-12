package it.publisys.pagamentionline.config;

import it.publisys.pagamentionline.ViewMappings;
import it.publisys.pagamentionline.handler.SpringExceptionResolver;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author mcolucci
 */
@Configuration
@ComponentScan(basePackages = "it.publisys.pagamentionline")
@EnableWebMvc
public class MvcConfig
        extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
        registry.addResourceHandler("/files/**").addResourceLocations("/files/");
        registry.addResourceHandler("/favicon.ico").addResourceLocations("/");
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasenames("WEB-INF/classes/messages");
        return source;
    }

    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource());
        return validator;
    }

    /* ====================================== *
     *      VIEW
     * ====================================== */
    @Bean
    public ServletContextTemplateResolver templateResolver() {
        ServletContextTemplateResolver _resolver = new ServletContextTemplateResolver();
        _resolver.setPrefix("/WEB-INF/views/");
        _resolver.setSuffix(".html");
        _resolver.setTemplateMode("HTML5");
        _resolver.setCacheable(false);
        return _resolver;
    }

    @Bean
    public SpringTemplateEngine engine() {
        SpringTemplateEngine _engine = new SpringTemplateEngine();
        _engine.setTemplateResolver(templateResolver());
        Set<IDialect> _additionalDialects = new HashSet<>();
        _additionalDialects.add(new org.thymeleaf.extras.springsecurity3.dialect.SpringSecurityDialect());
        _engine.setAdditionalDialects(_additionalDialects);
        return _engine;
    }

    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver _viewResolver = new ThymeleafViewResolver();
        _viewResolver.setTemplateEngine(engine());
        _viewResolver.setOrder(1);
        //viewResolver.setViewNames(new String[]{"*.html", "*.xhtml"});
        //viewResolver.setViewNames(new String[]{"*"});
        return _viewResolver;
    }

    @Override
    public void addArgumentResolvers(
            List<HandlerMethodArgumentResolver> argumentResolvers) {
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
        resolver.setFallbackPageable(new PageRequest(0, 15));
        resolver.setOneIndexedParameters(true);
        //resolver.setMaxPageSize(5);
        resolver.setPageParameterName("page");
        resolver.setSizeParameterName("size");
        argumentResolvers.add(resolver);
    }


    /* ====================================== *
     *      CONVERSION SERVICE
     * ====================================== */
//    @Bean
//    public FormattingConversionServiceFactoryBean conversionService() {
//        FormattingConversionServiceFactoryBean _conversion = new FormattingConversionServiceFactoryBean();
//
//        Set _set = new HashSet();
//        _set.add(new DateFormatter("dd/MM/yyyy"));
//
//        _conversion.setFormatters(_set);
//
//        return _conversion;
//    }

    /* ====================================== *
     *      EXCEPTION
     * ====================================== */
    @Bean
    public HandlerExceptionResolver handlerExceptionResolver() {
        SpringExceptionResolver exceptionResolver = new SpringExceptionResolver();
        exceptionResolver.setDefaultStatusCode(500);
        exceptionResolver.setDefaultErrorView(ViewMappings.ERROR_GENERIC);

        Properties mappings = new Properties();
        mappings.put(org.thymeleaf.exceptions.TemplateInputException.class.getName(), ViewMappings.ERROR_404);
        mappings.put(org.thymeleaf.exceptions.TemplateEngineException.class.getName(), ViewMappings.ERROR_404);
        mappings.put(java.lang.Exception.class.getName(), ViewMappings.ERROR_GENERIC);
        exceptionResolver.setExceptionMappings(mappings);
        /*
         Properties codes = new Properties();
         codes.put("/page/error/500", "500");
         codes.put("/page/error/404", "404");
         exceptionResolver.setStatusCodes(codes);
         */
        return exceptionResolver;
    }

}
