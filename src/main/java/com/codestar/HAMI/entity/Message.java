package com.codestar.HAMI.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "messages")
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@EntityListeners(AuditingEntityListener.class)
public class Message {
    @Id
    @Hidden
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    @Size(max = 1000)
    @NotBlank
    @NotEmpty
    private String text;

    @CreatedDate
    private Instant createdAt;

    @Column(length = 10_000_000)
    @Size(max = 10_000_000)
    private byte[] file;

    @Hidden
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @Hidden
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
}
