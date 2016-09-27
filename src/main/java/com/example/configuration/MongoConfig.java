package com.example.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClient;

/**
 * MongoConfig class used to make Mongo DB connection. 
 * 
 *
 * @author Prag Kamra
 * @author vinay Yadav
 * @author Seema Makkar
 * @author vivek 
 * @author Rishabh Jain
 *
 */


@Configuration
@PropertySource(value = {"classpath:application.properties"})
public class MongoConfig {

	@Value("${spring.data.mongodb.host}")
	private String mongoHost;

	@Value("${spring.data.mongodb.port}")
	private String mongoPort;

	@Value("${spring.data.mongodb.database}")
	private String mongoDB;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
	
	public @Bean MongoDbFactory mongoDbFactory() throws Exception {
		System.out.println(mongoHost + ":" +mongoPort);
		return new SimpleMongoDbFactory(new MongoClient(mongoHost,Integer.parseInt(mongoPort)), mongoDB);
	}

	public @Bean MongoTemplate mongoTemplate() throws Exception {

		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
		return mongoTemplate;

	}

}
