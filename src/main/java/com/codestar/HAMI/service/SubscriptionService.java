package com.codestar.HAMI.service;

import com.codestar.HAMI.entity.Chat;
import com.codestar.HAMI.entity.ChatTypeEnum;
import com.codestar.HAMI.entity.Profile;
import com.codestar.HAMI.entity.Subscription;
import com.codestar.HAMI.repository.ChatRepository;
import com.codestar.HAMI.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class SubscriptionService {
    @Autowired
    SubscriptionRepository subscriptionRepository;
    @Autowired
    ChatRepository chatRepository;

    public List<Subscription> getSubscriptionsByProfileId(Profile profile) {

        return subscriptionRepository.findByProfileId(profile.getId());
    }

    public Subscription saveSubscription(Subscription subscription) {
        return subscriptionRepository.saveAndFlush(subscription);
    }

    public boolean hasSubscription(Chat chat, Profile profile) {
        List<Subscription> subscriptions = subscriptionRepository
                .findByProfile_Id(profile.getId());
        long subCount = subscriptions.stream()
                .filter(subscription -> Objects
                        .equals(subscription.getChat().getId(), chat.getId())
                )
                .count();
        return subCount > 0;
    }

    public Subscription getSubscription(Chat chat, Profile profile) {
        List<Subscription> subscriptions = subscriptionRepository
                .findByProfile_Id(profile.getId());
        return subscriptions.stream()
                .filter(
                        subscription -> Objects
                                .equals(subscription.getChat().getId(), chat.getId())
                )
                .toList()
                .get(0);
    }

    public void createSubscription(Chat chat, Profile profile) {
        if (hasSubscription(chat, profile))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already Subscribed");
        Subscription subscription = Subscription
                .builder()
                .chat(chat)
                .profile(profile)
                .build();
        subscriptionRepository.save(subscription);
    }

    public void deleteSubscription(Chat chat, Profile profile) {
        if (!hasSubscription(chat, profile))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription Not Found");
        subscriptionRepository.delete(getSubscription(chat, profile));
    }

    public void createSubscription(Chat chat, ArrayList<Profile> profiles) {
        profiles.removeAll(Collections.singleton(null));
        profiles.forEach(profile -> createSubscription(chat, profile));
    }

    public List<Chat> getChatsByProfile(Profile profile) {
        return subscriptionRepository
                .findByProfile_Id(profile.getId())
                .stream()
                .map(Subscription::getChat)
                .toList();
    }

    public Long updateLastSeenMessage(Chat chat, Profile profile, Long messageId) {
        if(!hasSubscription(chat, profile))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This subscription doesn't exist.");
        Subscription subscription = getSubscription(chat, profile);
        subscription.setLastSeenMessageId(messageId);
        subscriptionRepository.save(subscription);

        return subscription.getLastSeenMessageId();
    }

    public List<Subscription> getSubscriptionsByChatId(Long chatId) {
        return subscriptionRepository.findByChatId(chatId);
    }

    public List<Long> intersectionSubscription(Profile profile1, Profile profile2) {

        List<Long> sub1 = new ArrayList<>();
        getChatsByProfile(profile1).forEach(chat -> sub1.add(chat.getId()));
        List<Long> sub2 = new ArrayList<>();
        getChatsByProfile(profile2).forEach(chat -> sub2.add(chat.getId()));



        List<Long> intersection = new ArrayList<>();

        for(Long chatId: sub1) {
            if(sub2.contains(chatId)) {
                intersection.add(chatId);
            }
        }
        System.out.println("count: " + (long) intersection.size());
        return intersection;
    }

    public boolean isPv(List<Long> chatIds) {
        for(Long chatId: chatIds) {
            if(chatRepository.findById(chatId).get().getChatType() == ChatTypeEnum.PV)
                return true;
        }
        return false;
    }

}
