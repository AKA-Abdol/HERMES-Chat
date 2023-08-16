package com.codestar.HAMI.entity;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

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

    @OneToMany(orphanRemoval = true, cascade = CascadeType.PERSIST)
    @Hidden
    private Set<Message> messages = new HashSet<>();

    public void removeMessage(Message message) {
        messages.remove(message);
    }
}