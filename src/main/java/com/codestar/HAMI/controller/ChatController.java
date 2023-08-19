package com.codestar.HAMI.controller;

import com.codestar.HAMI.entity.Chat;
import com.codestar.HAMI.entity.ChatTypeEnum;
import com.codestar.HAMI.entity.Profile;
import com.codestar.HAMI.model.ChatModel;
import com.codestar.HAMI.model.CreateChannelRequest;
import com.codestar.HAMI.model.CreateGroupRequest;
import com.codestar.HAMI.repository.ChatRepository;
import com.codestar.HAMI.repository.ProfileRepository;
import com.codestar.HAMI.service.ChatService;
import com.codestar.HAMI.service.ProfileService;
import com.codestar.HAMI.service.SubscriptionService;
import com.codestar.HAMI.service.UserAuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/chat")
public class ChatController {
    @Autowired
    ChatService chatService;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    UserAuthenticationService userAuthenticationService;
    @Autowired
    SubscriptionService subscriptionService;
    @Autowired
    ProfileService profileService;

    @GetMapping("/{chatId}")
    public ChatModel getChat(@PathVariable long chatId) {
        Chat chat = chatService.getChatById(chatId);
        if (chat == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found chat by chatId.");
        if(chat.getChatType() == ChatTypeEnum.PV)
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "you can't access with chatId.");

        return ChatModel.builder()
                .chatId(chat.getId())
                .bio(chat.getBio())
                .chatType(chat.getChatType())
                .description(chat.getDescription())
                .photo(chat.getPhoto())
                .build();
    }

    @PutMapping("/{chatId}")
    public ChatModel updateChat(@PathVariable Long chatId, @RequestBody Chat chatDetail) {
        Chat chat = chatService.updateChat(chatId, chatDetail);
        if (chat == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat id doesn't exist!");
        if(chat.getChatType() == ChatTypeEnum.PV)
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "you can't access with chatId.");

        return ChatModel.builder()
                .chatId(chat.getId())
                .bio(chat.getBio())
                .chatType(chat.getChatType())
                .description(chat.getDescription())
                .photo(chat.getPhoto())
                .build();
    }

    @PostMapping("/channel")
    public ChatModel createChannel(@Valid @RequestBody CreateChannelRequest request) {
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        Chat chat = chatService.createChatForChannel(request.getName(), request.getPhoto(), request.getDescription());

        ArrayList<Profile> profiles = new ArrayList<>(
                request
                        .getProfileIds()
                        .stream()
                        .map(profileId -> profileService.getProfileById(profileId))
                        .toList());
        profiles.add(profile);
        subscriptionService.createSubscription(chat, profiles);

        subscriptionService.createSubscription(chat, profiles);

        return ChatModel.builder()
                .chatId(chat.getId())
                .bio(chat.getBio())
                .chatType(chat.getChatType())
                .description(chat.getDescription())
                .photo(chat.getPhoto())
                .build();
    }

    @PostMapping("/Group")
    public ChatModel createGroup(@Valid @RequestBody CreateGroupRequest request) {
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        Chat chat = chatService.createChatForGroup(request.getName(), request.getPhoto());

        ArrayList<Profile> profiles = new ArrayList<>(
                request
                        .getProfileIds()
                        .stream()
                        .map(profileId -> profileService.getProfileById(profileId))
                        .toList());
        profiles.add(profile);
        subscriptionService.createSubscription(chat, profiles);

        return ChatModel.builder()
                .chatId(chat.getId())
                .bio(chat.getBio())
                .chatType(chat.getChatType())
                .description(chat.getDescription())
                .photo(chat.getPhoto())
                .build();
    }

    @PostMapping("/PV")
    public ChatModel createPv(@Valid @RequestBody Long profileId) {
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        Chat chat = chatService.createChatForPv(profileId);

        subscriptionService.createSubscription(chat, profile);
        subscriptionService.createSubscription(chat, profileService.getProfileById(profileId));

        return ChatModel.builder()
                .chatId(chat.getId())
                .bio(chat.getBio())
                .chatType(chat.getChatType())
                .description(chat.getDescription())
                .photo(chat.getPhoto())
                .build();
    }

}
