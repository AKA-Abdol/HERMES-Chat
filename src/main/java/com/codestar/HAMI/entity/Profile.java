package com.codestar.HAMI.entity;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "profiles")
@Setter
@Getter
public class Profile {
    @Id
    @Hidden
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 40)
    @Size(min = 3, max = 40)
    private String username;

    @Column(length = 60, nullable = false)
    @NotEmpty
    @NotBlank
    @Size(min = 3, max = 50)
    private String firstName;

    @Column(length = 60)
    private String lastName;

    @Column(length = 100)
    private String bio;

    private byte[] picture;

    @OneToOne(mappedBy = "profile")
    @Hidden
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "profile")
    @Hidden
    private Set<Subscription> subscriptions = new HashSet<>();

    @Hidden
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "profile")
    private Set<Message> messages = new HashSet<>();

    public void removeMessage(Message message) {
        messages.remove(message);
    }
}
