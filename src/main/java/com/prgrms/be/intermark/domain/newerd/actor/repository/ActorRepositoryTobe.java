package com.prgrms.be.intermark.domain.newerd.actor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prgrms.be.intermark.domain.newerd.actor.model.ActorTobe;

@Repository
public interface ActorRepositoryTobe extends JpaRepository<ActorTobe, Long> {
	Optional<ActorTobe> findActorByName(String name);
}
