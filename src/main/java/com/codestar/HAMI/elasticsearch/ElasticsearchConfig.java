package com.codestar.HAMI.elasticsearch;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

//@Configuration
//@EnableElasticsearchRepositories(basePackages = "com.codestar.HAMI.elasticsearch.repository")
//@ComponentScan(basePackages = { "com.codestar.HAMI.elasticsearch.service" })
public abstract class ElasticsearchConfig extends AbstractElasticsearchConfiguration {
//    Ignore elastic (remove abstract for activating)
//    @Value("${application.elasticsearch.host}")
//    private String host="localhost";
//    @Bean
//    @Override
//    public RestHighLevelClient elasticsearchClient(){
//        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
//                .connectedTo(host+":9200")
//                .build();
//
//        return RestClients.create(clientConfiguration)
//                .rest();
//    }
}