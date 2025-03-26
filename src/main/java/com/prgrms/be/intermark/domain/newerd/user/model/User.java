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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import com.prgrms.be.intermark.domain.newerd.user.dto.UserResponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "user_tobe",
	uniqueConstraints = {@UniqueConstraint(name = "social_uk", columnNames = {"social_type", "social_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

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
	private SocialType socialType;

	@NotBlank
	@Column(name = "social_id", nullable = false, length = 64)
	private String socialId;

	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "role", nullable = false, length = 15)
	private UserRole role;

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

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setBirth(@Nullable LocalDate birth) {
		this.birth = birth;
	}

	public String getUserRoleKey() {
		return role.getKey();
	}

	public void deactivateUser() {
		this.deleted = true;
	}

	public void updateRole(UserRole role) {
		this.role = role;
	}

	public UserResponse toUserResponse() {
		return UserResponse.builder()
			.email(email)
			.nickname(nickname)
			.build();
	}

}
