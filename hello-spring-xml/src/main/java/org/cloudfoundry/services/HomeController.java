package org.cloudfoundry.services;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

    @Autowired(required = false) private DataSource dataSource;

    @Autowired(required = false) private RedisConnectionFactory redisConnectionFactory;

    @Autowired(required = false) private MongoDbFactory mongoDbFactory;

    @Autowired(required = false) private ConnectionFactory rabbitConnectionFactory;

    /**
     * Simply selects the home view to render by returning its name.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {
        CloudEnvironment cloudEnvironment = new CloudEnvironment();

        List<String> services = new ArrayList<String>();
        if (dataSource != null) {
            if (dataSource instanceof BasicDataSource) {
                services.add("Data Source: " + ((BasicDataSource) dataSource).getUrl());
            } else if (dataSource instanceof SimpleDriverDataSource) {
                services.add("Data Source: " + ((SimpleDriverDataSource) dataSource).getUrl());
            }
        }
        if (redisConnectionFactory != null) {
            services.add("Redis: " + ((JedisConnectionFactory) redisConnectionFactory).getHostName() + ":" + ((JedisConnectionFactory) redisConnectionFactory).getPort());
        }
        if (mongoDbFactory != null) {
            services.add("MongoDB: " + mongoDbFactory.getDb().getMongo().getAddress());
        }
        if (rabbitConnectionFactory != null) {
            services.add("RabbitMQ: " + rabbitConnectionFactory.getHost() + ":" + rabbitConnectionFactory.getPort());
        }
        model.addAttribute("services", services);
        String environmentName =  cloudEnvironment.isCloudFoundry() ? "Cloud" : "Local";
        model.addAttribute("environmentName", environmentName);
        return "home";
    }

    @RequestMapping("/env")
    public void env(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println("System Properties:");
        for (Map.Entry<Object, Object> property : System.getProperties().entrySet()) {
            out.println(property.getKey() + ": " + property.getValue());
        }
        out.println();
        out.println("System Environment:");
        for (Map.Entry<String, String> envvar : System.getenv().entrySet()) {
            out.println(envvar.getKey() + ": " + envvar.getValue());
        }
    }

}
