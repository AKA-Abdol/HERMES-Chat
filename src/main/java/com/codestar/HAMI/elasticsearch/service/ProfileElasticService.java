package com.codestar.HAMI.elasticsearch.service;

import com.codestar.HAMI.elasticsearch.model.ProfileElasticModel;
import com.codestar.HAMI.elasticsearch.repository.ProfileElasticRepository;
import com.codestar.HAMI.entity.Profile;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

//@Service
public class ProfileElasticService {

    private final String INDEX_NAME = "profile";

//    @Autowired @Qualifier("profileElastic")
//    ProfileElasticRepository profileElasticRepository;
//
//    @Autowired
//    ElasticsearchOperations elasticsearchOperations;

//    public void addProfileToIndex(Profile profile){
//        ProfileElasticModel profileElasticModel = ProfileElasticModel
//                .builder()
//                .id(profile.getId())
//                .username(profile.getUsername())
//                .build();
//        IndexQuery query = new IndexQueryBuilder()
//                .withId(profileElasticModel.getId().toString())
//                .withObject(profileElasticModel)
//                .build();
//        elasticsearchOperations.index(query, IndexCoordinates.of(INDEX_NAME));
//    }
//
//    public void removeProfileFromIndex(Profile profile){
//        profileElasticRepository.deleteById(profile.getId());
//    }
//
//    public List<ProfileElasticModel> matchProfilesWithUsername(String fieldValue) throws IOException {
//        QueryBuilder query = QueryBuilders
//                .matchQuery("username", fieldValue)
//                .fuzziness(Fuzziness.AUTO);
//
//        Query searchQuery = new NativeSearchQueryBuilder()
//                .withQuery(query)
//                .build();
//
//        SearchHits<ProfileElasticModel> searchHits =
//                elasticsearchOperations.search(searchQuery, ProfileElasticModel.class, IndexCoordinates.of(INDEX_NAME));
//
//        return searchHits.stream()
//                .map(SearchHit::getContent)
//                .collect(Collectors.toList());
//    }
}
