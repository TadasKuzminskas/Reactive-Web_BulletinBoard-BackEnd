package com.example.demo2.config.database;

import dev.miku.r2dbc.mysql.MySqlConnectionConfiguration;
import dev.miku.r2dbc.mysql.MySqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories
public class R2DBCconfig extends AbstractR2dbcConfiguration {

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;

    @Value("${db.name}")
    private String dbName;

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
       return MySqlConnectionFactory.from(MySqlConnectionConfiguration.builder()
               .host("127.0.0.1")
               .port(3306)
                       .username(dbUsername)
                       .password(dbPassword)
                       .database(dbName)
               .build());
    }

}
