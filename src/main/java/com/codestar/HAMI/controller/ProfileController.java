package com.codestar.HAMI.controller;

import com.codestar.HAMI.elasticsearch.model.ProfileElasticModel;
import com.codestar.HAMI.entity.Chat;
import com.codestar.HAMI.entity.ChatTypeEnum;
import com.codestar.HAMI.entity.Profile;
import com.codestar.HAMI.model.ProfileModel;
import com.codestar.HAMI.service.ChatService;
import com.codestar.HAMI.service.ProfileService;
import com.codestar.HAMI.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/profile")
public class ProfileController {

    @Autowired
    ProfileService profileService;

    @Autowired
    UserAuthenticationService userAuthenticationService;

    @Autowired
    ChatService chatService;

    @PostMapping()//TODO picture
    public ProfileModel createProfile(@RequestBody Profile profile) {
        Long userId = userAuthenticationService.getAuthenticatedUser().getId();
        profile = profileService.createProfile(profile, userId);
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No User Found To Create Profile");
        }
        System.out.println("profile is not null!");
        return ProfileModel
                .builder()
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .bio(profile.getBio())
                .username(profile.getUsername())
                .photo(profile.getPhoto())
                .build();
    }

    @GetMapping("/{profileId}")
    public ProfileModel getProfileById(@PathVariable Long profileId){
        Profile profile = profileService.getProfileById(profileId);
        if (profile == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No profile found");
        }
        return ProfileModel
                .builder()
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .bio(profile.getBio())
                .username(profile.getUsername())
                .photo(profile.getPhoto())
                .build();
    }

    @GetMapping("/me")
    public ProfileModel getMyProfile() {
        Profile profile = profileService.getLoggedInProfile();
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No profile found");
        }
        return ProfileModel
                .builder()
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .bio(profile.getBio())
                .username(profile.getUsername())
                .photo(profile.getPhoto())
                .build();
    }

    @GetMapping("/search")
    public List<ProfileElasticModel> getSearchedProfileAndChats(@RequestParam(required = true) String username){
        List<Profile> profiles = null;
        List<Chat> chats = null;
        List<ProfileElasticModel> result = null;
        try {
            profiles = profileService.getProfilesByUserNameFuzziness(username);
            chats = chatService.getChatsByUserNameFuzziness(username);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something were wrong");
        }
         result = profiles.stream()
                .map(profile -> ProfileElasticModel
                        .builder()
                        .id(profile.getId())
                        .username(profile.getUsername())
                        .photo(profile.getPhoto())
                        .build())
                .collect(Collectors.toList());
        result.addAll(
                chats.stream()
                        .filter(chat -> !chat.getChatType().equals(ChatTypeEnum.PV))
                        .map(chat -> ProfileElasticModel
                                .builder()
                                .id(chat.getId())
                                .username(chat.getName())
                                .picture(chat.getPhoto())
                                .chatType(chat.getChatType())
                                .build()
                        )
                        .toList()
        );
        return result;
    }

    @GetMapping("/username")
    public void isOccupiedUserName(@RequestParam(required = true) String search) {
        if (profileService.isOccupiedUserName(search)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "username is already used");
        }
    }

    @PutMapping("/{profileId}")
    public Profile updateProfile(
            @RequestBody Profile profileData, @PathVariable Long profileId
    ) {
        return profileService.updateProfile(profileData, profileId);
    }
}
