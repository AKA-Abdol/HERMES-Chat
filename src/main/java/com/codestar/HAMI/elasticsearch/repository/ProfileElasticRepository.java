package com.codestar.HAMI.elasticsearch.repository;

import com.codestar.HAMI.elasticsearch.model.ProfileElasticModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

//@Qualifier("profileElastic")
public interface ProfileElasticRepository{ // extends ElasticsearchRepository<ProfileElasticModel, Long> {

    void deleteById(Long id);
}
