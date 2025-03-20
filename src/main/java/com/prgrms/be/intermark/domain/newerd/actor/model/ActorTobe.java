package com.prgrms.be.intermark.domain.newerd.actor.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.prgrms.be.intermark.common.entity.BaseEntity;
import com.prgrms.be.intermark.domain.actor.model.Gender;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "actor_tobe")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ActorTobe extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(name = "name", nullable = false, length = 20)
	private String name;

	@NotNull
	@Column(name = "birth", nullable = false)
	private LocalDate birth;

	@NotNull
	@Enumerated(value = EnumType.STRING)
	@Column(name = "gender", nullable = false, length = 10)
	private Gender gender;

	@NotBlank
	@Column(name = "profile_image_url", nullable = false, length = 2000)
	private String profileImageUrl;

	@Builder
	public ActorTobe(String name, LocalDate birth, Gender gender, String profileImageUrl) {
		this.name = name;
		this.birth = birth;
		this.gender = gender;
		this.profileImageUrl = profileImageUrl;
	}
}
