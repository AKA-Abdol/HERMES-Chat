package com.codestar.HAMI.repository;

import com.codestar.HAMI.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("select m from Message m where m.chat.id = ?1 order by m.createdAt DESC")
    List<Message> findByChatIdOrderByCreatedAtDesc(Long id);

    @Query("select m from Message m  where m.id > ?1 ORDER BY id")
    List<Message> findMessagesAfterTheMessage(Long id);
}
