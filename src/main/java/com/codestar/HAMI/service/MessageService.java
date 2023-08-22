package com.codestar.HAMI.service;

import com.codestar.HAMI.entity.*;
import com.codestar.HAMI.repository.ChatRepository;
import com.codestar.HAMI.repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public List<Message> getChatMessages(Chat chat) {
        return messageRepository.findByChatIdOrderByCreatedAtDesc(chat.getId());
    }

    public List<Message> getChatMessagesByChatId(Long chatId) {
        return messageRepository.findByChatIdOrderByCreatedAtDesc(chatId);
    }

    public Message createMessage(
            Message message, Profile profile, Chat chat
    ) {
        message.setChat(chat);
        if (chat.getChatType() == ChatTypeEnum.PV || chat.getChatType() == ChatTypeEnum.GROUP)
            message.setViewCount(0L);
        if(chat.getChatType() == ChatTypeEnum.CHANNEL)
            message.setViewCount(1L);

        message.setProfile(profile);
        return messageRepository.save(message);
    }

    @Transactional
    public Message createChatAndMessage(Message message, Long profileId) {
        Chat chat = createPvChat();
        chat = chatService.createChat(chat);

        Profile loggedInProfile = profileService.getLoggedInProfile();
        Subscription subscription1 = createSubscription(chat, loggedInProfile.getId());
        Subscription subscription2 = createSubscription(chat, profileId);
        subscription1 = subscriptionService.saveSubscription(subscription1);
        subscription2 = subscriptionService.saveSubscription(subscription2);

        setMessageProfile(message, loggedInProfile);
        profileService.addSubscription(subscription1, loggedInProfile.getId());
        profileService.addSubscription(subscription2, profileId);

        message.setChat(chat);
        chat.getMessages().add(message);
        addChatSubscription(subscription1, subscription2, chat);
        loggedInProfile.getMessages().add(message);

        message = messageRepository.saveAndFlush(message);
        return message;
    }

    private void setMessageProfile(Message message, Profile profile) throws EntityNotFoundException {
        if (profile == null) {
            throw new EntityNotFoundException("No profile found");
        }
        message.setProfile(profile);
    }

    private Subscription createSubscription(Chat chat, Long profileId) {
        Subscription subscription = new Subscription();
        subscription.setProfile(profileService.getProfileById(profileId));
        subscription.setChat(chat);
        return subscription;
    }

    private void addChatSubscription(Subscription sender, Subscription receiver, Chat chat) {
        chat.getSubscriptions().add(sender);
        chat.getSubscriptions().add(receiver);
    }

    private Chat createPvChat() {
        Chat chat = new Chat();
        chat.setChatType(ChatTypeEnum.PV);
        return chat;
    }

    public void deleteMessage(Long messageId) {
        Message message = this.getMessageById(messageId);
        if (message == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message Not Found.");
        this.validateUserCanDeleteMessage(message);
        messageRepository.delete(message);
    }

    public Message getMessageById(Long messageId) {
        if (messageId == null)
            return null;
        return messageRepository.findById(messageId).orElse(null);
    }

    public Message editMessage(Long messageId, Message messageData, Profile profile) {
        Message message = getMessageById(messageId);

        if (message == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message Not Found");
        if (!canEditMessage(message, profile))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User Can't Edit The Message");

        message.setText(messageData.getText());
        message.setFile(messageData.getFile());
        return messageRepository.save(message);
    }

    private void validateUserCanDeleteMessage(Message message) {
        Profile profile = profileService.getLoggedInProfile();
        if (message.getChat().getChatType().equals(ChatTypeEnum.PV)
                && (!Objects.equals(profile.getId(), message.getProfile().getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Deleting message refused");
        }
        //TODO set user access level based on user role in group and channels
    }

    private boolean canEditMessage(Message message, Profile profile) {
        return message.getProfile().getId().equals(profile.getId());
        //TODO set user access level based on user role in group and channels
    }

    public Long countOfUnreadMessage(Chat chat, Profile profile, Long lastSeenMessageId) {
        List<Message> messages;
        if(lastSeenMessageId == null)
            messages = messageRepository.findAll();
        else
            messages = messageRepository.findMessagesAfterTheMessage(lastSeenMessageId);


        messages = messages.stream()
                .filter(message -> {
                            boolean A = message.getProfile().getId() != profile.getId();
                            boolean B = message.getChat().getId() == chat.getId();
                            return A && B;
                        }
                ).toList();
        return messages.stream().count();
    }

    public Long updateMessageView(Long messageId) {
        Message message = messageRepository.findById(messageId).orElse(null);

        if(message == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "message doesn't exist.");

        message.setViewCount(message.getViewCount() + 1);
        messageRepository.save(message);
        return message.getViewCount();
    }

    public Message saveForwardMessage(Message message, Subscription subscription, Profile senderProfile, Chat senderChat) {
        Message forwardMessage = createForwardMessage(message);
        forwardMessage.setSubscription(subscription);
        return createMessage(forwardMessage, senderProfile, senderChat);
    }

    private Message createForwardMessage(Message message){
        Message forwardMessage = new Message();
        forwardMessage.setText(message.getText());
        forwardMessage.setFile(message.getFile());
        return forwardMessage;
    }
}
