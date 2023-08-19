package com.codestar.HAMI.service;

import com.codestar.HAMI.entity.*;
import com.codestar.HAMI.repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    @Autowired
    UserAuthenticationService userAuthenticationService;

    public List<Message> getChatMessagesByChatId(Long chatId) {
        return messageRepository.findByChatIdOrderByCreatedAtDesc(chatId);
    }

    public Message createMessage(Message message, Long chatId) {
        Chat chat = chatService.getChatById(chatId);
        this.setMessageProfile(message, profileService.getLoggedInProfile());
        message.setChat(chat);
        chat.getMessages().add(message);
        message = messageRepository.saveAndFlush(message);
        return message;
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
        this.validateUserCanDeleteMessage(message);
        this.deleteMessageFromChat(message);
        messageRepository.flush();
    }

    private void deleteMessageFromChat(Message message) {
        Chat chat = message.getChat();
        chat.removeMessage(message);
    }

    public Message getMessageById(Long messageId) {
        return messageRepository.findById(messageId).orElse(null);
    }

    public void editMessage(Long messageId, Message message) throws EntityNotFoundException {
        Message mainMessage = this.getMessageById(messageId);
        if (mainMessage == null) {
            throw new EntityNotFoundException("No message found");
        }
        this.validateUserCanEditMessage(mainMessage);
        mainMessage.setText(message.getText());
        mainMessage.setFile(message.getFile());
        messageRepository.save(mainMessage);
    }

    private void validateUserCanDeleteMessage(Message message) {
        Profile profile = profileService.getLoggedInProfile();
        if (message.getChat().getChatType().equals(ChatTypeEnum.PV)
                && (!Objects.equals(profile.getId(), message.getProfile().getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Deleting message refused");
        }
        //TODO set user access level based on user role in group and channels
    }

    private void validateUserCanEditMessage(Message message) {
        Profile profile = profileService.getLoggedInProfile();
        if (message.getChat().getChatType().equals(ChatTypeEnum.PV)
                && (!Objects.equals(profile.getId(), message.getProfile().getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Editing message refused");
        }
        //TODO set user access level based on user role in group and channels
    }
}
