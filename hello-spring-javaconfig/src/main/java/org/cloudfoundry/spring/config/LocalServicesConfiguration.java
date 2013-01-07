package org.cloudfoundry.spring.config;

import com.mongodb.Mongo;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;

@Configuration
@Profile("default")
public class LocalServicesConfiguration {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder().build();
    }

    @Bean
    public MongoDbFactory simpleMongoDbFactory() throws Exception {
        return new SimpleMongoDbFactory(new Mongo(), "hello");
    }

    @Bean
    public RedisConnectionFactory redis() {
        return new JedisConnectionFactory();
    }

    @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        return new CachingConnectionFactory();
    }
}
