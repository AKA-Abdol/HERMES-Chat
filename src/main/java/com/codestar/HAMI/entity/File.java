package com.codestar.HAMI.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "files")
@Getter
@Setter
public class File {

    @Id
    @Hidden
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String ContentType;

    @Column
    private byte[] data;

    @Hidden
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "photo")
    private List<Profile> profiles;

    @Hidden
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "photo")
    private List<Chat> chats;

    @Hidden
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "file")
    private List<Message> messages;

}


