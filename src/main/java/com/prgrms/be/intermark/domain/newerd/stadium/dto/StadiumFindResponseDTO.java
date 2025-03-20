package com.prgrms.be.intermark.domain.newerd.stadium.dto;

import com.prgrms.be.intermark.domain.newerd.stadium.model.StadiumTobe;

import lombok.Builder;

@Builder
public record StadiumFindResponseDTO(String name, String address, String imageUrl) {

	public static StadiumFindResponseDTO from(StadiumTobe stadium) {
		return StadiumFindResponseDTO.builder()
			.name(stadium.getName())
			.address(stadium.getAddress())
			.imageUrl(stadium.getImageUrl())
			.build();
	}
}
