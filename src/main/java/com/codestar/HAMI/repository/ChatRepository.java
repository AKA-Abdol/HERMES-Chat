package com.codestar.HAMI.repository;

import com.codestar.HAMI.entity.Chat;
import com.codestar.HAMI.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

}
