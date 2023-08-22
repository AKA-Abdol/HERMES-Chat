package com.codestar.HAMI.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subscriptions")
@Setter @Getter
@Hidden
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column
    private Long lastSeenMessageId;

    @Hidden
    @JsonIgnore
    @OneToMany(mappedBy = "subscription")
    private Set<Message> messages = new HashSet<>();

    @JsonIgnore
    @Hidden
    public String getFullName() {
        return profile.getFirstName() + " " + profile.getLastName();
    }

    @PreRemove
    public void deleteMessages(){
        this.messages.forEach(message -> message.setSubscription(null));
    }
}
