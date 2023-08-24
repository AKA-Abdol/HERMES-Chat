package com.codestar.HAMI.entity;

import com.codestar.HAMI.model.MessagePreview;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
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

    @Hidden
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "photo_id")
    private File photo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chat")
    @Hidden
    private Set<Subscription> subscriptions = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chat", cascade = CascadeType.ALL)
    @Hidden
    private Set<Message> messages = new HashSet<>();

    @Hidden
    @JsonIgnore
    private Long creatorProfileId;

    @Hidden
    @JsonIgnore
    private Long pinnedMessageId;

    public void removeMessage(Message message) {
        messages.remove(message);
    }

    public String getName(Profile profile) {
        if (chatType != ChatTypeEnum.PV)
            return name;
        Profile destinationProfile = getPVProfile(profile);
        if (destinationProfile == null)
            return null;
        return destinationProfile.getFirstName() + " " + destinationProfile.getLastName();
    }

    private Profile getPVProfile(Profile profile) {
        return subscriptions
                .stream()
                .map(Subscription::getProfile)
                .filter(subProfile -> !Objects.equals(
                        subProfile.getId(), profile.getId()
                ))
                .toList()
                .get(0);
    }

    public Long getSubscriptionChatId(Profile profile) {
        if (chatType == ChatTypeEnum.PV)
            return getPVProfile(profile).getId();
        return id;
    }

    @Hidden
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

    @Hidden
    @JsonIgnore
    public byte[] getPhoto() {
        if (photo == null)
            return null;
        return photo.getData();
    }
}