package it.publisys.pagamentionline.config;

import it.publisys.spring.mail.MailEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *
 * @author mcolucci
 */
@Configuration
@PropertySource("classpath:configuration.properties")
@ImportResource("classpath:spring/spring-quartz.xml")
public class AppConfig
        extends WebMvcConfigurerAdapter {

    @Autowired
    private Environment env;

    /* ====================================== *
     *      Mailer
     * ====================================== */
    @Bean
    public MailEngine mailEngine() {
        String _mailProtocol = env.getRequiredProperty("mail.protocol");
        String _mailHost = env.getRequiredProperty("mail.host");
        Integer _mailPort = env.getRequiredProperty("mail.port", Integer.class);
        boolean _mailAuth = env.getRequiredProperty("mail.auth", Boolean.class);
        String _mailUsername = env.getRequiredProperty("mail.username");
        String _mailPassword = env.getRequiredProperty("mail.password");
        String _mailFrom = env.getRequiredProperty("mail.from");
        boolean _mailSsl = env.getRequiredProperty("mail.ssl", Boolean.class);
        //String _mailSupport = env.getRequiredProperty("mail.support");

        JavaMailSenderImpl _sender = new JavaMailSenderImpl();

        _sender.setHost(_mailHost);
        _sender.setPort(_mailPort);
        _sender.setProtocol(_mailProtocol);
        if (_mailAuth) {
            _sender.setUsername(_mailUsername);
            _sender.setPassword(_mailPassword);
        }
        _sender.getJavaMailProperties().setProperty("mail.smtp.from", _mailFrom);
        if (_mailSsl) {
            _sender.getJavaMailProperties().setProperty("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
        }
        return new MailEngine(_sender);
    }

}
