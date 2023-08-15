package com.codestar.HAMI.service;

import com.codestar.HAMI.entity.Profile;
import com.codestar.HAMI.entity.Subscription;
import com.codestar.HAMI.entity.User;
import com.codestar.HAMI.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileService {

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    UserService userService;

    public Profile createProfile(Profile profile, long userId){
        User user = userService.getUserById(userId);
        if (user == null){
            return null;
        }
        profile.setUser(user);
        user.setProfile(profile);
        return profileRepository.saveAndFlush(profile);
    }

    public Profile getProfileByProfileId(Long profileId) {
        return profileRepository.findById(profileId).orElse(null);
    }

    public List<Profile> getProfilesByUserNamePrefix(String username) {
        return profileRepository.findByUsernameStartsWithIgnoreCase(username);
    }

    public void addSubscription(Subscription subscription, long profileId) {
        Profile profile = this.getProfileByProfileId(profileId);
        profile.getSubscriptions().add(subscription);
        profileRepository.save(profile);
    }
}
