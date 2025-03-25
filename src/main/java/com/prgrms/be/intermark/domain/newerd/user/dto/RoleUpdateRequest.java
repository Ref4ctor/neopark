package com.prgrms.be.intermark.domain.newerd.user.dto;

import javax.validation.constraints.NotBlank;

import com.prgrms.be.intermark.domain.newerd.user.model.UserRoleTobe;

public record RoleUpdateRequest(
	@NotBlank UserRoleTobe role
) {
}
