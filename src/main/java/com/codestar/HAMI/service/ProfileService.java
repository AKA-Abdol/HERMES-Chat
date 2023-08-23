package com.codestar.HAMI.service;

import com.codestar.HAMI.entity.File;
import com.codestar.HAMI.elasticsearch.model.ProfileElasticModel;
import com.codestar.HAMI.elasticsearch.service.ProfileElasticService;
import com.codestar.HAMI.entity.Profile;
import com.codestar.HAMI.entity.Subscription;
import com.codestar.HAMI.entity.User;
import com.codestar.HAMI.repository.ProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Transactional
    public Profile createProfile(Profile profile, long userId, Long chatId) throws IOException {
        User user = userService.getUserById(userId);
        if (user == null) {
            return null;
        }
        profile.setUser(user);
        profile.setSelfChatId(chatId);
        user.setProfile(profile);
        profile = profileRepository.saveAndFlush(profile);
        System.out.println("Before elastic call");
        profileElasticService.addProfileToIndex(profile);
        System.out.println("After elastic call");
        return profile;
    }

    public Profile getProfileById(Long profileId) {
        return profileRepository.findById(profileId).orElse(null);
    }

    public List<Profile> getProfilesByUserNameFuzziness(String username) throws IOException {
        List<ProfileElasticModel> profileElasticModels =  profileElasticService.matchProfilesWithUsername(username);
        List<Profile> profiles = new ArrayList<>();
        for(ProfileElasticModel profileElasticModel: profileElasticModels){
            profiles.add(this.getProfileById(profileElasticModel.getId()));
        }
        return profiles;
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

    public void changeProfilePhoto(Profile profile, File photo) {
        profile.setPhoto(photo);
        profileRepository.save(profile);
    }

    public void deleteProfilePhoto(Profile profile) {
        profile.setPhoto(null);
        profileRepository.save(profile);
    }
}
