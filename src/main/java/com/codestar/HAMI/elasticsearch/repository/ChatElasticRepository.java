package com.codestar.HAMI.elasticsearch.repository;

import com.codestar.HAMI.elasticsearch.model.ChatElasticModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ChatElasticRepository extends ElasticsearchRepository<ChatElasticModel, Long> {
}
