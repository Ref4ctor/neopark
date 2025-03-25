package com.prgrms.be.intermark.domain.newerd.user.service;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.be.intermark.domain.newerd.user.model.UserTobe;
import com.prgrms.be.intermark.domain.newerd.user.repository.UserRepositoryTobe;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserValidationService {

	private final UserRepositoryTobe userRepositoryTobe;

	public UserTobe findUser(Long userId) {
		return userRepositoryTobe.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException("존재하지 않은 유저입니다."));
	}

	public UserTobe findActiveUser(Long userId) {
		return userRepositoryTobe.findByIdAndDeletedFalse(userId)
			.orElseThrow(() -> new EntityNotFoundException("존재하지 않은 유저입니다."));
	}

	public void checkIsExist(Long userId) {
		boolean isExist = userRepositoryTobe.existsById(userId);
		if (!isExist) {
			throw new EntityNotFoundException("해당 아이디를 가진 유저가 존재하지 않습니다.");
		}
	}

	public Page<UserTobe> findAllActiveUsers(Pageable pageable) {
		return userRepositoryTobe.findByDeletedFalse(pageable);
	}

}
