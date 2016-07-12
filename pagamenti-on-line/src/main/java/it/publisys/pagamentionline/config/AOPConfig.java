package it.publisys.pagamentionline.config;


import it.publisys.pagamentionline.aop.LoggingAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *
 * @author mcolucci
 */
@Configuration
@ImportResource("classpath:spring/spring-aop.xml")
public class AOPConfig
    extends WebMvcConfigurerAdapter {

    @Bean
    public LoggingAspect logAspect() {
        return new LoggingAspect();
    }

}
