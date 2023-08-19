package com.codestar.HAMI.elasticsearch.repository;

import com.codestar.HAMI.elasticsearch.model.ProfileElasticModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProfileElasticRepository extends ElasticsearchRepository<ProfileElasticModel, Long> {
}
