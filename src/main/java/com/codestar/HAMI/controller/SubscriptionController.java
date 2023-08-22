package com.codestar.HAMI.controller;

import com.codestar.HAMI.entity.Chat;
import com.codestar.HAMI.entity.Message;
import com.codestar.HAMI.entity.Profile;
import com.codestar.HAMI.entity.Subscription;
import com.codestar.HAMI.model.ProfilesSubscriptionRequest;
import com.codestar.HAMI.model.SubscriptionResponse;
import com.codestar.HAMI.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
                                .image(null)
                                .name(chat.getName(profile))
                                .chatType(chat.getChatType())
                                .lastMessage(chat.getLastMessagePreview())
                                .chatId(chat.getId())
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
