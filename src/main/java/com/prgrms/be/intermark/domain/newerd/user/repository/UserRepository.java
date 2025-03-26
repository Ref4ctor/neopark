package com.prgrms.be.intermark.domain.newerd.user.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.be.intermark.domain.newerd.user.model.SocialType;
import com.prgrms.be.intermark.domain.newerd.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByIdAndDeletedFalse(Long userId);

	Page<User> findByDeletedFalse(Pageable pageable);

	Optional<User> findBySocialTypeAndSocialIdAndDeletedFalse(SocialType socialType, String socialId);

	Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

	Optional<User> findByIdAndRefreshToken(Long userId, String refreshToken);
}
