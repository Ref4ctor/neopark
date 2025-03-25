package com.prgrms.be.intermark.domain.newerd.user.dto;

import lombok.Builder;

@Builder
public record UserResponse(
	String nickname,
	String email
) {
}
