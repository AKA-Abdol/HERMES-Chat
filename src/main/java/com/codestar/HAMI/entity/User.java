package com.codestar.HAMI.entity;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Setter
@Getter
public class User implements UserDetails {
    @Id
    @Hidden
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(unique = true, nullable = false)
    @Email
    private String email;

    @Column(nullable = false, length = 64)
    @Schema(hidden = false)
    @Size(min = 8, max = 64)
    private String password;

    @OneToOne()
    @Hidden
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private Profile profile;

    @Override
    @Hidden
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    @Hidden
    public String getUsername() {
        return String.valueOf(id);
    }

    @Override
    @Hidden
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Hidden
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Hidden
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Hidden
    public boolean isEnabled() {
        return true;
    }
}
