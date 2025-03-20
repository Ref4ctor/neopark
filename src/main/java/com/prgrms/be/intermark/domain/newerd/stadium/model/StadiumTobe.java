package com.prgrms.be.intermark.domain.newerd.stadium.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.prgrms.be.intermark.common.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stadium_tobe")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StadiumTobe extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(name = "name", nullable = false)
	private String name;

	@NotBlank
	@Column(name = "address", nullable = false, unique = true)
	private String address;

	@NotBlank
	@Column(name = "image_url", nullable = false, length = 2000)
	private String imageUrl;

	@Builder
	public StadiumTobe(String name, String address, String imageUrl) {
		this.name = name;
		this.address = address;
		this.imageUrl = imageUrl;
	}
}
