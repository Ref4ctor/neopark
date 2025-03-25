package com.prgrms.be.intermark.domain.newerd.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.be.intermark.domain.newerd.user.dto.UserResponse;
import com.prgrms.be.intermark.domain.newerd.user.model.UserRoleTobe;
import com.prgrms.be.intermark.domain.newerd.user.model.UserTobe;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceTobe {

	private final UserValidationService userValidationService;

	public UserResponse getActiveUser(Long userId) {
		return userValidationService.findActiveUser(userId).toUserResponse();
	}

	public Page<UserResponse> getAllActiveUser(Pageable pageable) {
		return userValidationService.findAllActiveUsers(pageable).map(UserTobe::toUserResponse);
	}

	@Transactional
	public void deactivateUser(Long userId, Long targetId) {
		UserTobe userTobe = userValidationService.findActiveUser(userId);
		validatePermission(userTobe, targetId);
		userTobe.deactivateUser();
	}

	@Transactional
	public void updateRole(Long userId, UserRoleTobe role) {
		UserTobe userTobe = userValidationService.findActiveUser(userId);
		userTobe.updateRole(role);
	}

	private void validatePermission(UserTobe userTobe, Long targetId) {
		if (userTobe.getRole() == UserRoleTobe.ROLE_USER && !userTobe.getId().equals(targetId)) {
			throw new AccessDeniedException("권한이 없습니다: 다른 사용자를 비활성화할 수 없습니다.");
		}
	}

}
