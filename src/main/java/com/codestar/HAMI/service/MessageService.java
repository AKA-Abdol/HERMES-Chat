package com.codestar.HAMI.service;

import com.codestar.HAMI.entity.*;
import com.codestar.HAMI.repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    ProfileService profileService;

    @Autowired
    ChatService chatService;

    @Autowired
    SubscriptionService subscriptionService;

    public List<Message> getChatMessagesByChatId(Long chatId) {
        return messageRepository.findByChatIdOrderByCreatedAtDesc(chatId);
    }

    public Message createMessage(Message message, Long chatId) {
        Chat chat = chatService.getChatById(chatId);
        this.setProfile(message, 1L); //TODO get profileId from jwt
        message.setChat(chat);
        message = messageRepository.saveAndFlush(message);
        chat.getMessages().add(message);
        chatService.createChat(chat);
        return message;
    }

    public Message createChatAndMessage(Message message, Long profileId) {
        Chat chat = this.createPvChat();
        chat = chatService.createChat(chat);
        Subscription subscription1 = this.createSubscription(chat, 1L); //TODO get profile id by jwt
        Subscription subscription2 = this.createSubscription(chat, profileId);
        subscription1 = subscriptionService.saveSubscription(subscription1);
        subscription2 = subscriptionService.saveSubscription(subscription2);
        this.setProfile(message, 1L); //TODO get profileId from jwt
        profileService.addSubscription(subscription1, 1L); //TODO get profileId from jwt
        profileService.addSubscription(subscription2, profileId);
        message.setChat(chat);
        message = messageRepository.saveAndFlush(message);
        chat.getMessages().add(message);
        chat.getSubscriptions().add(subscription1);
        chat.getSubscriptions().add(subscription2);
        chat = chatService.createChat(chat);
        //TODO add message to profile

        return message;
    }

    private void setProfile(Message message, Long profileId) throws EntityNotFoundException{
        Profile profile = profileService.getProfileByProfileId(profileId);
        if (profile == null){
            throw new EntityNotFoundException("No profile found");
        }
        message.setProfile(profile);
    }

    private Subscription createSubscription(Chat chat, Long profileId){
        Subscription subscription = new Subscription();
        subscription.setProfile(profileService.getProfileByProfileId(profileId));
        subscription.setChat(chat);
        return subscription;
    }

    private Chat createPvChat(){
        Chat chat = new Chat();
        chat.setChatType(ChatTypeEnum.PV);
        return chat;
    }

    public void deleteMessage(Long messageId) {
        Message message = this.getMessageById(messageId);
        this.deleteMessageFromChat(message);
        messageRepository.flush();
        //TODO deleteMessageFromProfile
    }

    public void deleteMessageFromChat(Message message) {
        Chat chat = message.getChat();
        chat.removeMessage(message);
    }

    public Message getMessageById(Long messageId){
        return messageRepository.findById(messageId).orElse(null);
    }

    public void editMessage(Long messageId, Message message) throws EntityNotFoundException {
        Message mainMessage = messageRepository.findById(messageId).orElse(null);
        if (mainMessage == null){
            throw new EntityNotFoundException("No message found");
        }
        mainMessage.setText(message.getText());
        mainMessage.setFile(message.getFile());
        messageRepository.save(mainMessage);
    }
}
