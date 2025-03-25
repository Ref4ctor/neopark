package com.prgrms.be.intermark.domain.newerd.user.controller;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.be.intermark.common.dto.ResponseDTO;
import com.prgrms.be.intermark.domain.newerd.user.dto.RoleUpdateRequest;
import com.prgrms.be.intermark.domain.newerd.user.dto.UserResponse;
import com.prgrms.be.intermark.domain.newerd.user.service.UserServiceTobe;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v2/users")
@RequiredArgsConstructor
public class UserControllerTobe {

	private final UserServiceTobe userServiceTobe;

	@GetMapping("/{userId}")
	public ResponseEntity<ResponseDTO<UserResponse>> getActiveUser(@PathVariable Long userId) {
		UserResponse res = userServiceTobe.getActiveUser(userId);
		return ResponseEntity.ok().body(ResponseDTO.success(res));
	}

	@GetMapping
	public ResponseEntity<ResponseDTO<Page<UserResponse>>> getAllActiveUser(Pageable pageable) {
		Page<UserResponse> res = userServiceTobe.getAllActiveUser(pageable);
		return ResponseEntity.ok().body(ResponseDTO.success(res));
	}

	@PostMapping("/{userId}:deactivate")
	public ResponseEntity<ResponseDTO<?>> deactivateUser(@PathVariable Long userId,
		@AuthenticationPrincipal User user) {
		userServiceTobe.deactivateUser(Long.valueOf(user.getUsername()), userId);
		return ResponseEntity.ok().body(ResponseDTO.success());

	}

	//TODO 관리자만 가능하도록
	@PatchMapping("/{userId}")
	public ResponseEntity<Object> updateRole(@PathVariable Long userId,
		@Valid @RequestBody RoleUpdateRequest roleUpdateRequest) {
		userServiceTobe.updateRole(userId, roleUpdateRequest.role());
		return ResponseEntity.ok().build();
	}
}
