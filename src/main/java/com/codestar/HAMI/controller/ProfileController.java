package com.codestar.HAMI.controller;

import com.codestar.HAMI.entity.Profile;
import com.codestar.HAMI.model.ProfileModel;
import com.codestar.HAMI.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/profile")
public class ProfileController{

    @Autowired
    ProfileService profileService;

    @PostMapping()//TODO picture
    public ProfileModel createProfile(@Valid @RequestBody Profile profile){
        profile = profileService.createProfile(profile, 1L);
        if (profile == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No User Found To Create Profile");
        }
        return ProfileModel
                .builder()
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .bio(profile.getBio())
                .username(profile.getUsername())
                .picture(profile.getPicture())
                .build();
    }

    @GetMapping("/{profileId}")
    public ProfileModel getProfileById(@PathVariable Long profileId){
        Profile profile = profileService.getProfileByProfileId(profileId);
        if (profile == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No profile found");
        }
        return ProfileModel
                .builder()
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .bio(profile.getBio())
                .username(profile.getUsername())
                .picture(profile.getPicture())
                .build();
    }

    @GetMapping("/me")
    public ProfileModel getMyProfile(){
        Profile profile = profileService.getProfileByProfileId(1L);//TODO getProfileId by jwt
        if (profile == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No profile found");
        }
        return ProfileModel
                .builder()
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .bio(profile.getBio())
                .username(profile.getUsername())
                .picture(profile.getPicture())
                .build();
    }

    @GetMapping("/search")
    public List<ProfileModel> getSearchedProfile(@RequestParam(required = true) String username){
        List<Profile> profiles = profileService.getProfilesByUserNamePrefix(username);
        return profiles.stream()
                .map(profile -> ProfileModel
                        .builder()
                        .firstName(profile.getFirstName())
                        .lastName(profile.getLastName())
                        .bio(profile.getBio())
                        .username(profile.getUsername())
                        .picture(profile.getPicture())
                        .build())
                .collect(Collectors.toList());
    }
}
