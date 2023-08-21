package com.codestar.HAMI.service;

import com.codestar.HAMI.entity.Profile;
import com.codestar.HAMI.entity.Subscription;
import com.codestar.HAMI.entity.User;
import com.codestar.HAMI.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ProfileService {

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    UserService userService;

    @Autowired
    UserAuthenticationService userAuthenticationService;

    public Profile createProfile(Profile profile, long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return null;
        }
        profile.setUser(user);
        user.setProfile(profile);
        profile = profileRepository.saveAndFlush(profile);
//        profileElasticService.addProfileToIndex(profile); Ignore elastic
        return profile;
    }

    public Profile getProfileById(Long profileId) {
        return profileRepository.findById(profileId).orElse(null);
    }

    public List<Profile> getProfilesByUserNameFuzziness(String username) throws IOException {
//        Ignore elastic
//        List<ProfileElasticModel> searchResponse =  profileElasticService.matchProfilesWithUsername(username);
//        List<Profile> listOfProducts  = new ArrayList<>();
//        for(ProfileElasticModel profileElasticModel : searchResponse){
//            Long ProfileId = profileElasticModel.getId();
//            listOfProducts.add(this.getProfileById(ProfileId));
//        }

        return getProfilesByUserNamePrefix(username);
    }

    public List<Profile> getProfilesByUserNamePrefix(String username) {
        return profileRepository.findByUsernameStartsWithIgnoreCase(username);
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

    public Profile updateProfile(Profile profileData, Profile profile) {
        profile.setProfile(profileData);
        return profileRepository.save(profile);
    }
}
