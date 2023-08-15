package com.codestar.HAMI.repository;

import com.codestar.HAMI.entity.Profile;
import com.codestar.HAMI.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query("select s from Subscription s where s.profile.id = ?1")
    List<Subscription> findByProfileId(Long id);

}
