package com.prgrms.be.intermark.domain.newerd.user.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import com.prgrms.be.intermark.common.entity.BaseEntity;
import com.prgrms.be.intermark.domain.newerd.user.dto.UserResponse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_tobe")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserTobe extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Email
	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Length(min = 2, max = 25)
	@NotBlank
	@Column(name = "nickname", nullable = false, length = 25)
	private String nickname;

	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "social_type", nullable = false)
	private SocialTypeTobe socialTypeTobe;

	@NotBlank
	@Column(name = "social_id", nullable = false, length = 64)
	private String socialId;

	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "role", nullable = false, length = 15)
	private UserRoleTobe role;

	@Nullable
	@Column(name = "refresh_token", unique = true)
	private String refreshToken;

	@Column(name = "is_deleted", nullable = false)
	private boolean deleted;

	@Nullable
	@Column(name = "birth")
	private LocalDate birth;

	public void activateUser() {
		this.deleted = false;
	}

	public void deactivateUser() {
		this.deleted = true;
	}

	public void updateRole(UserRoleTobe role) {
		this.role = role;
	}

	public UserResponse toUserResponse() {
		return UserResponse.builder()
			.email(email)
			.nickname(nickname)
			.build();
	}

}
