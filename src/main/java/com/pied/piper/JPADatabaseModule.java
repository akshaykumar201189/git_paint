package com.pied.piper;

import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;

import java.util.Properties;

/**
 * Created by akshay.kesarwan on 21/07/16.
 */
public class JPADatabaseModule extends AbstractModule {
    private String JPA_UNIT = "pied-piper";

    @Override
    protected void configure() {
        Properties properties = getProperties();
        install(new JpaPersistModule(JPA_UNIT).properties(properties));
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        properties.put("hibernate.connection.url","jdbc:mysql://localhost:3306/pied_piper?autoReconnect=true");
        properties.put("hibernate.connection.username", "root");
        properties.put("hibernate.connection.password", "");
        properties.put("hibernate.connection.pool_size", "5");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.connection.autocommit","false");
        properties.put("hibernate.connection.isolation","2");
        properties.put("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");
        return properties;
    }
}
