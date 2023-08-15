package com.codestar.HAMI.service;

import com.codestar.HAMI.entity.Chat;
import com.codestar.HAMI.entity.Profile;
import com.codestar.HAMI.entity.Subscription;
import com.codestar.HAMI.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {
    @Autowired
    ChatRepository chatRepository;

    @Autowired
    ProfileService profileService;

    @Autowired
    SubscriptionService subscriptionService;


    public List<Chat> getAllChats(Long profileId) {
        Profile profile = profileService.getProfileByProfileId(profileId);
        if(profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Profile Found with profile id.");
        }

        List<Subscription> subscriptions = subscriptionService.getSubscriptionsByProfileId(profile);

        List<Chat> chats = new ArrayList<>();
        for (Subscription subscription: subscriptions) {
            chats.add(subscription.getChat());
        }

        return chats;

    }

    public Chat getChatById(long chatId) {
        Chat chat = chatRepository.findById(chatId).orElse(null);
        return chat;
    }

    public Chat updateChat(Long chatId ,Chat chat) {
        Chat updateChat = chatRepository.findById(chatId)
                .orElse(null);

        System.out.println(updateChat.getId());

        if(updateChat != null) {
            updateChat.setBio(chat.getBio());
            updateChat.setChatType(chat.getChatType());
            updateChat.setDescription(chat.getDescription());

            chatRepository.save(updateChat);
        }

        return updateChat;
    }

    public Chat createChat(Chat chat) {
        return chatRepository.save(chat);
    }

}
