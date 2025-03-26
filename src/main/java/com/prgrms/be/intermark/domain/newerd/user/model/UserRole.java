package com.prgrms.be.intermark.domain.newerd.user.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
	ROLE_USER("ROLE_USER", "회원"),
	ROLE_ADMIN("ROLE_ADMIN", "관리자");

	private final String key;
	private final String title;
}
