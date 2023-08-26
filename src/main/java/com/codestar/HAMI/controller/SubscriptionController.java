package com.codestar.HAMI.controller;

import com.codestar.HAMI.entity.*;
import com.codestar.HAMI.model.ProfilesSubscriptionRequest;
import com.codestar.HAMI.model.SubscriptionResponse;
import com.codestar.HAMI.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Autowired
    private MessageService messageService;

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
        if(chat.getChatType() == ChatTypeEnum.PV) {
            //subscriptionService.deleteSubscription(chat, profile);

            //System.out.println("111111111111111111111111");
//            Profile profilePv1 = subscriptionService.getSubscriptionsByChatId(chatId).get(0).getProfile();
//            System.out.println("222222222222222222222222");
//            Profile profilePv2 = subscriptionService.getSubscriptionsByChatId(chatId).get(1).getProfile();
//            System.out.println("333333333333333333333333");
//
//            System.out.println("profilepv1 = " + profilePv1.getId());
//            System.out.println("profilepv2 = " + profilePv2.getId());
//
//            if(Objects.equals(profilePv1.getId(), profile.getId()))
//                subscriptionService.deleteSubscription(chat, profilePv2);
//            else
//                subscriptionService.deleteSubscription(chat, profilePv1);

            chatService.deleteChat(chat);
        }
        else
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
                        .map(profileId -> profileService.getProfileById(profileId))
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
                                .photo(chat.getPhoto())
                                .name(chat.getName(profile))
                                .chatType(chat.getChatType())
                                .lastMessage(chat.getLastMessagePreview())
                                .unSeenCount(messageService.countOfUnreadMessage(chat, profile, subscriptionService.getSubscription(chat, profile).getLastSeenMessageId()))
                                .chatId(chat.getId())
                                .profileId(chat.getPVProfileId(profile))
                                .build()
                )
                .toList();
    }

    @GetMapping("/unread-message/{chatId}")
    public Long getCountOfUnreadMessage(@PathVariable Long chatId) {
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        Chat chat = chatService.getChatById(chatId);
        if(!subscriptionService.hasSubscription(chat, profile))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This subscription doesn't exist.");
        Subscription subscription = subscriptionService.getSubscription(chat, profile);

        return messageService.countOfUnreadMessage(chat, profile, subscription.getLastSeenMessageId());
    }

    @PutMapping("/last-seen-message/{messageId}")
    public Long changeLastSeenMessage(@PathVariable Long messageId) {
        Profile profile = userAuthenticationService.getAuthenticatedProfile();
        Message message = messageService.getMessageById(messageId);
        Chat chat = chatService.getChatById(message.getChat().getId());

        return subscriptionService.updateLastSeenMessage(chat, profile, messageId);
    }


}
