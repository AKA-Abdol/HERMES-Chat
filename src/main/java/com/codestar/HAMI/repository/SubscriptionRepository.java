package com.codestar.HAMI.repository;

import com.codestar.HAMI.entity.Profile;
import com.codestar.HAMI.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByProfile_Id(Long id);

    @Query("select s from Subscription s where s.profile.id = ?1")
    List<Subscription> findByProfileId(Long id);

    @Query("select s from Subscription s where s.chat.id = ?1")
    List<Subscription> findByChatId(Long id);

}
