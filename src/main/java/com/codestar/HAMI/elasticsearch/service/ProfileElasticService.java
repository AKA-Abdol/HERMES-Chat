package com.codestar.HAMI.elasticsearch.service;

import co.elastic.clients.elasticsearch.core.search.Hit;
import com.codestar.HAMI.elasticsearch.model.ProfileElasticModel;
import com.codestar.HAMI.elasticsearch.repository.ProfileElasticRepository;
import com.codestar.HAMI.entity.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//@Service
public class ProfileElasticService {

    private final String INDEX_NAME = "profile";

//    @Autowired
//    ProfileElasticRepository profileElasticRepository;
//
//    public void addProfileToIndex(Profile profile) throws IOException {
//        ProfileElasticModel profileElasticModel = ProfileElasticModel
//                .builder()
//                .id(profile.getId())
//                .username(profile.getUsername())
//                .build();
//        profileElasticRepository.createOrUpdate(profileElasticModel);
//    }
//
//    public void removeProfileFromIndex(Profile profile) throws IOException {
//        profileElasticRepository.deleteById(profile.getId());
//    }
//
//    public List<ProfileElasticModel> matchProfilesWithUsername(String fieldValue) throws IOException {
//        List<Hit<ProfileElasticModel>> listOfHits = profileElasticRepository.searchWithFuzziness(fieldValue);
//        List<ProfileElasticModel> profileElasticModels  = new ArrayList<>();
//        for(Hit<ProfileElasticModel> hit : listOfHits){
//            profileElasticModels.add(hit.source());
//        }
//        return profileElasticModels;
//    }
}
