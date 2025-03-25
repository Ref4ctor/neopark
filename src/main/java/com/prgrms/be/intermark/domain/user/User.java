package com.prgrms.be.intermark.domain.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import com.prgrms.be.intermark.domain.ticket.model.Ticket;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user",
	uniqueConstraints = {@UniqueConstraint(name = "social_uk", columnNames = {"social_type", "social_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Email
	@Column(name = "email", nullable = false, unique = true) // 일단 length 제약 없이 두기
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
	private boolean isDeleted;

	@Nullable
	@Column(name = "birth")
	private LocalDate birth;

	@OneToMany(mappedBy = "user")
	private List<Ticket> tickets = new ArrayList<>();

	@Builder
	public User(SocialType social, String socialId, String nickname, UserRole role, String email) {
		this.socialType = social;
		this.socialId = socialId;
		this.nickname = nickname;
		this.role = role;
		this.isDeleted = false;
		this.email = email;
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

	public void deleteUser() {
		this.isDeleted = true;
	}

	public boolean isDeleted() {
		return this.isDeleted;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public void activateUser() {
		this.isDeleted = false;
	}
}
