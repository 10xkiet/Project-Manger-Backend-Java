package com.group8.projectmanager.repositories;

import com.group8.projectmanager.models.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.stream.Stream;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Stream<Invitation> findByReceiverId(Long id);
}