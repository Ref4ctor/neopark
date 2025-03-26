package com.prgrms.be.intermark.domain.newerd.user.dto;

import com.prgrms.be.intermark.domain.newerd.user.model.UserRole;

public record UserIdAndRoleDTO(
	Long userId,
	UserRole userRole
) {
}
