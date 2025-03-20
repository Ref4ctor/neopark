package com.prgrms.be.intermark.domain.newerd.actor.dto;

import com.prgrms.be.intermark.domain.newerd.actor.model.ActorTobe;

import lombok.Builder;

@Builder
public record ActorResponseDTO(String name, String profileImage) {

	public static ActorResponseDTO from(ActorTobe actor) {
		return ActorResponseDTO.builder()
			.name(actor.getName())
			.profileImage(actor.getProfileImageUrl())
			.build();
	}

	// 필요시 구현
   /* public static List<ActorResponseDTO> listFromCastings(List<CastingInfo> castings) {

        return castings.stream()
                .map(casting -> ActorResponseDTO.from(casting.getActor()))
                .toList();
    }*/
}
