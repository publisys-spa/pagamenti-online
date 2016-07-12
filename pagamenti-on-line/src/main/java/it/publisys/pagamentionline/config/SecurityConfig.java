package it.publisys.pagamentionline.config;

import it.publisys.pagamentionline.handler.LoginAuthenticationSuccessHandler;
import it.publisys.pagamentionline.service.login.LoginUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@ImportResource("classpath:spring/spring-security.xml")
public class SecurityConfig
        extends WebMvcConfigurerAdapter {
    
    @Bean
    public LoginUserDetailsService loginUserDetailsService() {
        return new LoginUserDetailsService();
    }
    
    @Bean
    public LoginAuthenticationSuccessHandler loginAuthenticationSuccessHandler() {
        return new LoginAuthenticationSuccessHandler();
    }
}
