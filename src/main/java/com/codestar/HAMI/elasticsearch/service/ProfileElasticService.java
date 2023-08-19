package com.codestar.HAMI.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.codestar.HAMI.elasticsearch.model.ProfileElasticModel;
import com.codestar.HAMI.elasticsearch.repository.ProfileElasticRepository;
import com.codestar.HAMI.elasticsearch.util.ElasticSearchUtil;
import com.codestar.HAMI.entity.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.function.Supplier;

@Service
public class ProfileElasticService {

    @Autowired
    ProfileElasticRepository profileElasticRepository;

    @Autowired
    ElasticsearchClient  elasticsearchClient;

    public void addProfileToIndex(Profile profile){
        ProfileElasticModel profileElasticModel = ProfileElasticModel
                .builder()
                .id(profile.getId())
                .username(profile.getUsername())
                .build();
        profileElasticRepository.save(profileElasticModel);
    }

    public void removeProfileFromIndex(Profile profile){
        ProfileElasticModel profileElasticModel = ProfileElasticModel
                .builder()
                .id(profile.getId())
                .build();
        profileElasticRepository.delete(profileElasticModel);
    }

    public SearchResponse<ProfileElasticModel> matchProfilesWithUsername(String fieldValue) throws IOException {
        Supplier<Query> supplier  = ElasticSearchUtil.supplierWithNameField(fieldValue);
        SearchResponse<ProfileElasticModel> searchResponse = elasticsearchClient.search(s->s.index("profile").query(supplier.get()),ProfileElasticModel.class);
        return searchResponse;
    }
}
