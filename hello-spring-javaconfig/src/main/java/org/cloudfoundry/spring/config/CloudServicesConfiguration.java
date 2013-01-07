package org.cloudfoundry.spring.config;

import org.cloudfoundry.runtime.env.*;
import org.cloudfoundry.runtime.service.document.MongoServiceCreator;
import org.cloudfoundry.runtime.service.keyvalue.RedisServiceCreator;
import org.cloudfoundry.runtime.service.messaging.RabbitServiceCreator;
import org.cloudfoundry.runtime.service.relational.RdbmsServiceCreator;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile("cloud")
public class CloudServicesConfiguration {

    private CloudEnvironment cloudEnvironment = new CloudEnvironment();

    @Bean
    public DataSource dataSource() {
        RdbmsServiceInfo rdbmsServiceInfo = lookupService(RdbmsServiceInfo.class, null);
        return new RdbmsServiceCreator().createService(rdbmsServiceInfo);
    }

    @Bean
    public MongoDbFactory simpleMongoDbFactory() throws Exception {
        MongoServiceInfo mongoServiceInfo = lookupService(MongoServiceInfo.class, null);
        return new MongoServiceCreator().createService(mongoServiceInfo);
    }

    @Bean
    public RedisConnectionFactory redis() {
        RedisServiceInfo redisServiceInfo = lookupService(RedisServiceInfo.class, null);
        return new RedisServiceCreator().createService(redisServiceInfo);
    }

    @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        RabbitServiceInfo rabbitServiceInfo = lookupService(RabbitServiceInfo.class, null);
        return new RabbitServiceCreator().createService(rabbitServiceInfo);
    }

    /**
     * looks up services bound to the application based on type and, optionally, service name.
     * @param classOfT the class (e.g., {@link RabbitServiceInfo})
     * @param serviceName the name that the service is bound under. this is useful if, for example, the type alone can't be used to disambiguate bound services (if you have two RDBMses, for example.)
     * @param <T> a subclass of {@link AbstractServiceInfo}
     * @return a ready to use instance of {@link T}
     */
    private <T extends AbstractServiceInfo> T lookupService(Class<T> classOfT, String serviceName) {
        assert classOfT != null : "you must provide a lookupService class type";
        List<T> listOfTs = new ArrayList<T>();
        if (StringUtils.hasText(serviceName)) {
            listOfTs.add(cloudEnvironment.getServiceInfo(serviceName, classOfT));
        } else {
            listOfTs.addAll(cloudEnvironment.getServiceInfos(classOfT));
        }
        assert listOfTs.size() == 1 : "There should be only one match for service type " + classOfT.getName() +
                ((StringUtils.hasText(serviceName)) ? " and service name " + serviceName : "") + ".";
        return listOfTs.iterator().next();
    }
}
