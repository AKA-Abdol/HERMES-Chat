package com.codestar.HAMI.repository;

import com.codestar.HAMI.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Profile findByUsernameIgnoreCase(String username);
    List<Profile> findByUsernameStartsWithIgnoreCase(String username);
}
