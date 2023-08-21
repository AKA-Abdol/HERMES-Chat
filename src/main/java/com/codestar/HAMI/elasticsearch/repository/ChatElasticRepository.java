package com.codestar.HAMI.elasticsearch.repository;

import com.codestar.HAMI.elasticsearch.model.ChatElasticModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

//@Qualifier("chatElastic")
public interface ChatElasticRepository{// extends ElasticsearchRepository<ChatElasticModel, Long> {

    void deleteById(Long id);
}
