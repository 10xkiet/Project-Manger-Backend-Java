package com.group8.projectmanager.repositories;

import com.group8.projectmanager.models.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Stream<Invitation> findByReceiverId(long id);
    Stream<Invitation> findBySenderId(long id);
}