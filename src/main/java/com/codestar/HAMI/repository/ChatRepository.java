package com.codestar.HAMI.repository;

import com.codestar.HAMI.entity.Chat;
import com.codestar.HAMI.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByNameStartsWithIgnoreCase(String name);

}
