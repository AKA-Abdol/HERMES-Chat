package com.codestar.HAMI.service;

import com.codestar.HAMI.entity.*;
import com.codestar.HAMI.elasticsearch.model.ChatElasticModel;
import com.codestar.HAMI.entity.Chat;
import com.codestar.HAMI.entity.ChatTypeEnum;
import com.codestar.HAMI.entity.Profile;
import com.codestar.HAMI.entity.Subscription;

import com.codestar.HAMI.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.codestar.HAMI.elasticsearch.service.ChatElasticService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ChatService {
    @Autowired
    ChatRepository chatRepository;

    @Autowired
    ProfileService profileService;

    @Autowired
    SubscriptionService subscriptionService;

    final public String SELF_CHAT_NAME = "saved message";


    public List<Chat> getAllChats(Long profileId) {
        Profile profile = profileService.getProfileById(profileId);
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Profile Found with profile id.");
        }

        List<Subscription> subscriptions = subscriptionService.getSubscriptionsByProfileId(profile);

        List<Chat> chats = new ArrayList<>();
        for (Subscription subscription : subscriptions) {
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

    public Chat updateChat(Long chatId, Chat chat) throws IOException {
        Chat updateChat = chatRepository.findById(chatId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat Not Found!")
                );

        updateChat.setName(chat.getName());
        updateChat.setChatType(chat.getChatType());
        updateChat.setDescription(chat.getDescription());

        if (updateChat != null) {
            updateChat.setName(chat.getName());
            updateChat.setChatType(chat.getChatType());
            updateChat.setDescription(chat.getDescription());

            chatRepository.save(updateChat);
//            chatElasticService.addChatToIndex(updateChat);
        }

        return updateChat;
}

    public Chat createChat(Chat chat){
        chat = chatRepository.saveAndFlush(chat);
//        try {
//            chatElasticService.addChatToIndex(chat);
//        } catch (IOException e) {
//            System.out.println("ELASTIC SEARCH ERROR ***");
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
//        }
        return chat;
    }

    public Chat createChatForChannel(
            String name, File photo, String description, Long creatorProfileId
    ) {
        Chat chat = new Chat();
        chat.setName(name);
        chat.setPhoto(photo);
        chat.setDescription(description);
        chat.setChatType(ChatTypeEnum.CHANNEL);
        chat.setCreatorProfileId(creatorProfileId);
        return createChat(chat);
    }

    public Chat createChatForGroup(
            String name, String description, File photo, Long creatorProfileId
    ) {
        Chat chat = new Chat();
        chat.setName(name);
        chat.setPhoto(photo);
        chat.setDescription(description);
        chat.setChatType(ChatTypeEnum.GROUP);
        chat.setCreatorProfileId(creatorProfileId);
        return chatRepository.save(chat);
    }

    public Chat createChatForPv(Long profileId) {
        Chat chat = new Chat();
        Profile profile = profileService.getProfileById(profileId);
        chat.setName(profile.getFirstName() + " " + profile.getLastName());
        chat.setPhoto(null);
        chat.setChatType(ChatTypeEnum.PV);
        return chatRepository.save(chat);
    }

    public List<Chat> getChatsByUserNameFuzziness(String username) throws IOException {
//        List<ChatElasticModel> chatElasticModels =  chatElasticService.matchChatsWithUsername(username);
//        List<Chat> chats = new ArrayList<>();
//        for(ChatElasticModel chatElasticModel: chatElasticModels){
//            chats.add(this.getChatById(chatElasticModel.getId()));
//        }
        return getChatsByUserNamePrefix(username);
    }

    public List<Chat> getChatsByUserNamePrefix(String username) {
        return chatRepository.findByNameStartsWithIgnoreCase(username);
    }

    private boolean canPinMessage(Profile profile, Chat chat) {
        return Objects.equals(profile.getId(), chat.getCreatorProfileId());
    }

    private void savePinnedMessage(Chat chat, Long messageId) {
        chat.setPinnedMessageId(messageId);
        chatRepository.save(chat);
    }

    public void pinMessage(Profile profile, Chat chat, Long messageId) {
        if (chat.getChatType() == ChatTypeEnum.PV) {
            savePinnedMessage(chat, messageId);
            return;
        }
        if (!canPinMessage(profile, chat))
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Only Creator Can Pin A Message"
            );
        savePinnedMessage(chat, messageId);
    }

    public void unpinMessage(Profile profile, Chat chat) {
        if (chat.getChatType() == ChatTypeEnum.PV) {
            savePinnedMessage(chat, null);
            return;
        }
        if (!canPinMessage(profile, chat))
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Only Creator Can Pin A Message"
            );
        savePinnedMessage(chat, null);
    }

    public Chat createSelfChat() {
        Chat chat = new Chat();
        chat.setName(SELF_CHAT_NAME);
        chat.setChatType(ChatTypeEnum.SELF);
        return createChat(chat);
    }

    public void setChatPhoto(Chat chat, File photo) {
        chat.setPhoto(photo);
        chatRepository.save(chat);
    }

    public void deleteChat(Chat chat) {
        chatRepository.delete(chat);
    }
}
