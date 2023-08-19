package com.codestar.HAMI.controller;

import com.codestar.HAMI.entity.Chat;
import com.codestar.HAMI.entity.Profile;
import com.codestar.HAMI.model.ProfilesSubscriptionRequest;
import com.codestar.HAMI.model.SubscriptionResponse;
import com.codestar.HAMI.service.ChatService;
import com.codestar.HAMI.service.ProfileService;
import com.codestar.HAMI.service.SubscriptionService;
import com.codestar.HAMI.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/subscribe")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private ChatService chatService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @PostMapping("/{chatId}")
    public void subscribeChat(@PathVariable Long chatId) {
        Chat chat = chatService.getChatById(chatId);
        // check if chatType is not PV
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        subscriptionService.createSubscription(chat, profile);
    }

    @DeleteMapping("/{chatId}")
    public void unSubscribeChat(@PathVariable Long chatId) {
        Chat chat = chatService.getChatById(chatId);
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        subscriptionService.deleteSubscription(chat, profile);
    }

    @PostMapping("/{chatId}/profiles")
    public void subscribeProfilesToChat(
            @PathVariable Long chatId,
            @RequestBody ProfilesSubscriptionRequest profilesSubscriptionRequest
    ) {
        ArrayList<Profile> profiles = new ArrayList<>(
                profilesSubscriptionRequest
                        .getProfiles()
                        .stream()
                        .map(profileId -> profileService.getProfileByProfileId(profileId))
                        .toList()
        );
        Chat chat = chatService.getChatById(chatId);
        subscriptionService.createSubscription(chat, profiles);
    }

    @GetMapping("")
    public List<SubscriptionResponse> getSubscriptions() {
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        List<Chat> chats = subscriptionService.getChatsByProfile(profile);
        return chats
                .stream()
                .map(
                        chat -> SubscriptionResponse
                                .builder()
                                .image(null)
                                .name(chat.getName(profile))
                                .chatType(chat.getChatType())
                                .lastMessage(chat.getLastMessagePreview())
                                .build()
                )
                .toList();
    }

}
