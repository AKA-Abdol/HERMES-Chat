package com.codestar.HAMI.entity;

import com.codestar.HAMI.model.ProfileRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "profiles")
@Setter
@Getter
@NoArgsConstructor
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

    @Hidden
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "photo_id")
    private File photo;

    @Hidden
    @JsonIgnore
    @OneToOne(mappedBy = "profile")
    private User user;

    @Hidden
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "profile")
    private Set<Subscription> subscriptions = new HashSet<>();

    @Hidden
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "profile")
    private Set<Message> messages = new HashSet<>();

    @Hidden
    @JsonIgnore
    @NotNull
    private Long selfChatId;

    public void removeMessage(Message message) {
        messages.remove(message);
    }

    @Hidden
    @JsonIgnore
    public String getFullName() {
        return firstName + " " + lastName;
    }
    @Hidden
    public void setProfile(Profile profileData) {
        username = profileData.getUsername();
        firstName = profileData.getFirstName();
        lastName = profileData.getLastName();
        bio = profileData.getBio();
    }
    public Profile(ProfileRequest profileData) {
        username = profileData.getUsername();
        firstName = profileData.getFirstName();
        lastName = profileData.getLastName();
        bio = profileData.getBio();
    }

    @Hidden
    @JsonIgnore
    public String getFullName(Profile profileData) {
        return profileData.getFirstName() + " " + profileData.getLastName();
    }

    @Hidden
    @JsonIgnore
    public byte[] getPhoto() {
        if (photo == null)
            return null;
        return photo.getData();
    }
}
