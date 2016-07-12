package it.publisys.pagamentionline.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import it.publisys.pagamentionline.domain.IEntity;
import java.beans.PropertyVetoException;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *
 * @author mcolucci
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("it.publisys.pagamentionline.repository")
@PropertySource("classpath:datasource.properties")

public class DBConfig
    extends WebMvcConfigurerAdapter {

    @Autowired
    private Environment env;

    /* ====================================== *
     *      DATABASE
     * ====================================== */
    @Bean
    public DataSource dataSource() {
        try {
            ComboPooledDataSource ds = new ComboPooledDataSource();
            ds.setDriverClass(env.getRequiredProperty("app.jdbc.driverClassName"));
            ds.setJdbcUrl(env.getRequiredProperty("app.jdbc.url"));
            ds.setUser(env.getRequiredProperty("app.jdbc.username"));
            ds.setPassword(env.getRequiredProperty("app.jdbc.password"));
            ds.setMaxStatements(50);
            ds.setMaxPoolSize(100);
            ds.setMinPoolSize(10);
            ds.setAcquireIncrement(5);
            ds.setPreferredTestQuery("SELECT 1;");
            ds.setIdleConnectionTestPeriod(60);
            //ds.setTestConnectionOnCheckin(true);
            //ds.setMaxIdleTimeExcessConnections(240);
            return ds;
        } catch (IllegalStateException | PropertyVetoException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
        DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
        lef.setDataSource(dataSource);
        lef.setJpaVendorAdapter(jpaVendorAdapter);
        lef.setPackagesToScan(IEntity.class.getPackage().getName());

        // Defines the Hibernate properties
        Properties jpaProperties = new Properties();
        jpaProperties.setProperty("hibernate.show_sql", env.getRequiredProperty("hibernate.show_sql"));
        jpaProperties.setProperty("hibernate.format_sql", env.getRequiredProperty("hibernate.format_sql"));
        jpaProperties.setProperty("hibernate.hbm2ddl.auto", env.getRequiredProperty("hibernate.hbm2ddl.auto"));
        jpaProperties.setProperty("hibernate.hbm2ddl.import_files", env.getRequiredProperty("hibernate.hbm2ddl.import_files"));
        //jpaProperties.setProperty("hibernate.hbm2ddl.import_files_sql_extractor", "org.hibernate.tool.hbm2ddl.MultipleLinesSqlCommandExtractor");

        lef.setJpaProperties(jpaProperties);

        return lef;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        //hibernateJpaVendorAdapter.setShowSql(false);
        //hibernateJpaVendorAdapter.setGenerateDdl(true);
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        return hibernateJpaVendorAdapter;
    }

    @Bean
    public PlatformTransactionManager transactionManager(
        EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
