package it.publisys.pagamentionline.config;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


/**
 *
 * @author vasta
 */
@Configuration
public class FreemarkerConfig
    extends WebMvcConfigurerAdapter {

    @Bean
    public freemarker.template.Configuration freemarkerConfiguration()
        throws IOException {

        freemarker.template.Configuration cfg = new freemarker.template.Configuration();

        File _f = null;
        URL _url = FreemarkerConfig.class.getClassLoader().getResource("freemarker/templates");
        if(_url != null) {
            String _path = _url.getFile();
            _f = new File(_path);
            System.out.println("Freemarker Templates: " +_f.getAbsolutePath());
        } else {
            System.out.println("NESSUNA DIRECTORY FREEMARKER TROVATA.");
        }
                
        cfg.setDirectoryForTemplateLoading(_f);
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        cfg.setIncompatibleImprovements(new Version(2, 3, 20));
        //
        cfg.setDefaultEncoding("UTF-8");
        cfg.setDateFormat("dd/MM/yyyy");
        cfg.setDateTimeFormat("dd/MM/yyyy HH:mm.ss");

        return cfg;

    }

}
