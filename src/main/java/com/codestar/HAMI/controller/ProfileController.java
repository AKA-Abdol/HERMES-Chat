package com.codestar.HAMI.controller;

import com.codestar.HAMI.elasticsearch.model.ChatElasticModel;
import com.codestar.HAMI.entity.Chat;
import com.codestar.HAMI.entity.ChatTypeEnum;
import com.codestar.HAMI.entity.File;
import com.codestar.HAMI.entity.Profile;
import com.codestar.HAMI.model.ProfileRequest;
import com.codestar.HAMI.model.ProfileModel;
import com.codestar.HAMI.service.ChatService;
import com.codestar.HAMI.service.FileService;
import com.codestar.HAMI.service.ProfileService;
import com.codestar.HAMI.service.UserAuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Autowired
    FileService fileService;

    @PostMapping()
    public ProfileModel createProfile(
            @RequestBody ProfileRequest profileData
    ) {
        Long userId = userAuthenticationService.getAuthenticatedUser().getId();
        Profile newProfile = new Profile(profileData);
        if (profileData.getPhotoId() != null) {
            File profilePhoto = fileService.getFileById(profileData.getPhotoId());
            newProfile.setPhoto(profilePhoto);
        }
            Profile profile;
        try {
            profile = profileService.createProfile(newProfile, userId);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something were wrong");
        }
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
    public ProfileModel getProfileById(@PathVariable Long profileId) {
        Profile profile = profileService.getProfileById(profileId);
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

    @GetMapping("/me")
    public ProfileModel getMyProfile() {
        Profile profile = profileService.getLoggedInProfile();
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No profile found");
        }
        System.out.println("profilePhoto|"+ Arrays.toString(profile.getPhoto()) +"|");
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
    public List<ChatElasticModel> getSearchedProfileAndChats(@RequestParam(required = true) String username) {
        Long userProfileId = userAuthenticationService.getAuthenticatedProfile().getId();
        List<Profile> profiles = null;
        List<Chat> chats = null;
        List<ChatElasticModel> result = null;
        if (username.length() < 3){
            return new ArrayList<>();
        }
        try {
            profiles = profileService.getProfilesByUserNameFuzziness(username);
            chats = chatService.getChatsByUserNameFuzziness(username);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something were wrong");
        }
        result = profiles.stream()
                .filter(profile -> !Objects.equals(profile.getId(), userProfileId)) // Skip profile with ID 5
                .map(profile -> ChatElasticModel
                        .builder()
                        .id(profile.getId())
                        .username(profile.getUsername())
                        .chatType(ChatTypeEnum.PV.toString())
                        .photo(profile.getPhoto())
                        .fullName(profile.getFullName(profile))
                        .build())
                .collect(Collectors.toList());
        result.addAll(
                chats.stream()
                        .filter(chat -> !chat.getChatType().equals(ChatTypeEnum.PV))
                        .map(chat -> ChatElasticModel
                                .builder()
                                .id(chat.getId())
                                .fullName(chat.getName())
                                .photo(chat.getPhoto())
                                .chatType(chat.getChatType().toString())
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

    @PutMapping("")
    public Profile updateProfile(
            @RequestBody Profile profileData
    ) {
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        return profileService.updateProfile(profileData, profile);
    }

    @PutMapping("/photo/{photoId}")
    public ResponseEntity<String> changeProfilePhoto(
            @Valid @PathVariable Long photoId
    ) {
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        File photo = fileService.getFileById(photoId);
        profileService.changeProfilePhoto(profile, photo);
        return ResponseEntity.ok("Profile Photo Changed Successfully");
    }

    @DeleteMapping("/photo")
    public ResponseEntity<String> deleteProfilePhoto() {
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        profileService.deleteProfilePhoto(profile);
        return ResponseEntity.ok("Profile Photo Deleted Successfully");
    }
}
