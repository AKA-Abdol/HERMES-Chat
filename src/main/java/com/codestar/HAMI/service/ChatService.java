package com.codestar.HAMI.service;

import com.codestar.HAMI.entity.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.codestar.HAMI.elasticsearch.model.ChatElasticModel;
import com.codestar.HAMI.elasticsearch.service.ChatElasticService;
import com.codestar.HAMI.entity.Chat;
import com.codestar.HAMI.entity.Profile;
import com.codestar.HAMI.entity.Subscription;
import com.codestar.HAMI.repository.ChatRepository;
import com.codestar.HAMI.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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

    @Autowired
    ChatElasticService chatElasticService;


    public List<Chat> getAllChats(Long profileId) {
        Profile profile = profileService.getProfileById(profileId);
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
        return chatRepository
                .findById(chatId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Chat Not Found!"
                ));
    }

    public Chat updateChat(Long chatId ,Chat chat) {
        Chat updateChat = chatRepository.findById(chatId)
                .orElse(null);

        System.out.println(updateChat.getId());

        if(updateChat != null) {
            updateChat.setName(chat.getName());
            updateChat.setBio(chat.getBio());
            updateChat.setChatType(chat.getChatType());
            updateChat.setDescription(chat.getDescription());

            chatRepository.save(updateChat);
            chatElasticService.addChatToIndex(updateChat);
        }

        return updateChat;
    }

    public Chat createChat(Chat chat) {
        chat = chatRepository.saveAndFlush(chat);
        chatElasticService.addChatToIndex(chat);
        return chat;
    }

    public Chat createChatForChannel(String name, File photo, String description) {
        Chat chat = null;
        chat.setName(name);
        chat.setPhoto(photo.getData());
        chat.setDescription(description);
        chat.setChatType(ChatTypeEnum.CHANNEL);
        return chatRepository.save(chat);
    }

    public Chat createChatForGroup(String name, File photo) {
        Chat chat = null;
        chat.setName(name);
        chat.setPhoto(photo.getData());
        chat.setChatType(ChatTypeEnum.GROUP);
        return chatRepository.save(chat);
    }

    public Chat createChatForPv(Long profileId) {
        Chat chat = null;
        Profile profile = profileService.getProfileById(profileId);
        chat.setName(profile.getFirstName() + " " + profile.getLastName());
        chat.setPhoto(profile.getPhoto());
        chat.setChatType(ChatTypeEnum.PV);
        return chatRepository.save(chat);
    }

    public List<Chat> getChatsByUserNameFuzziness(String username) throws IOException {
        SearchResponse<ChatElasticModel> searchResponse =  chatElasticService.matchChatsWithUsername(username);

        List<Hit<ChatElasticModel>> listOfHits= searchResponse.hits().hits();
        List<Chat> chats  = new ArrayList<>();
        for(Hit<ChatElasticModel> hit : listOfHits){
            Long chatId = hit.source().getId();
            chats.add(this.getChatById(chatId));
        }
        return chats;
    }
}
