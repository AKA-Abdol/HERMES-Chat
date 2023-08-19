package com.codestar.HAMI.service;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.codestar.HAMI.elasticsearch.model.ProfileElasticModel;
import com.codestar.HAMI.elasticsearch.service.ProfileElasticService;
import com.codestar.HAMI.entity.Profile;
import com.codestar.HAMI.entity.Subscription;
import com.codestar.HAMI.entity.User;
import com.codestar.HAMI.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProfileService {

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    UserService userService;

    @Autowired
    UserAuthenticationService userAuthenticationService;

    @Autowired
    ProfileElasticService profileElasticService;

    public Profile createProfile(Profile profile, long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return null;
        }
        profile.setUser(user);
        user.setProfile(profile);
        profile = profileRepository.saveAndFlush(profile);
        profileElasticService.addProfileToIndex(profile);
        return profile;
    }

    public Profile getProfileById(Long profileId) {
        return profileRepository.findById(profileId).orElse(null);
    }

    public List<Profile> getProfilesByUserNameFuzziness(String username) throws IOException {
        SearchResponse<ProfileElasticModel> searchResponse =  profileElasticService.matchProfilesWithUsername(username);

        List<Hit<ProfileElasticModel>> listOfHits= searchResponse.hits().hits();
        List<Profile> listOfProducts  = new ArrayList<>();
        for(Hit<ProfileElasticModel> hit : listOfHits){
            Long ProfileId = hit.source().getId();
            listOfProducts.add(this.getProfileById(ProfileId));
        }
        return listOfProducts;
    }

    public void addSubscription(Subscription subscription, long profileId) {
        Profile profile = this.getProfileById(profileId);
        profile.getSubscriptions().add(subscription);
        profileRepository.save(profile);
    }

    public Profile getLoggedInProfile() {
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        if (profile == null) {
            throw new EntityNotFoundException("Please log in into a profile");
        }
        return profile;
    }

    public boolean isOccupiedUserName(String username) {
        return profileRepository.findByUsernameIgnoreCase(username) != null;
    }

    public Profile updateProfile(Profile profileData, Long profileId) {
        Profile profile = profileRepository
                .findById(profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile Not Found!"));
        profile.setProfile(profileData);
        return profileRepository.save(profile);
    }
}
