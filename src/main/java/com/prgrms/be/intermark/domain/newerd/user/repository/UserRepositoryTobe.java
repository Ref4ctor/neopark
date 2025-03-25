package com.prgrms.be.intermark.domain.newerd.user.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.be.intermark.domain.newerd.user.model.UserTobe;

public interface UserRepositoryTobe extends JpaRepository<UserTobe, Long> {

	Optional<UserTobe> findByIdAndDeletedFalse(Long userId);

	Page<UserTobe> findByDeletedFalse(Pageable pageable);
}
