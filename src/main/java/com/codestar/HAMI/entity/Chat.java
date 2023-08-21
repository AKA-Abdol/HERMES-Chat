package com.codestar.HAMI.entity;

import com.codestar.HAMI.model.MessagePreview;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chats")
@Setter
@Getter
public class Chat {
    @Id
    @Hidden
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    @Size(min = 5, max = 100)
    private String name;

    @Column(length = 100)
    @Size(max = 100)
    private String bio;

    @Column(nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private ChatTypeEnum chatType;

    @Column(length = 200)
    @Size(max = 200)
    private String description;

    @Column(length = 10_000_000)
    @Size(max = 10_000_000)
    private byte[] photo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chat")
    @Hidden
    private Set<Subscription> subscriptions = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chat", cascade = CascadeType.ALL)
    @Hidden
    private Set<Message> messages = new HashSet<>();

    public void removeMessage(Message message) {
        messages.remove(message);
    }

    public String getName(Profile profile) {
        if (chatType != ChatTypeEnum.PV)
            return name;
        Profile destinationProfile = subscriptions
                .stream()
                .map(Subscription::getProfile)
                .filter(subscriptionProfile -> subscriptionProfile != profile)
                .toList()
                .get(0);
        return destinationProfile.getFirstName() + destinationProfile.getLastName();
    }

    public MessagePreview getLastMessagePreview() {
        if (messages.size() == 0)
            return null;

        return messages
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList()
                .get(messages.size() - 1)
                .getPreview();
    }
}