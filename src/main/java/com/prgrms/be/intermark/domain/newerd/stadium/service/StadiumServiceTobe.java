package com.prgrms.be.intermark.domain.newerd.stadium.service;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.be.intermark.domain.newerd.stadium.model.StadiumTobe;
import com.prgrms.be.intermark.domain.newerd.stadium.repository.StadiumRepositoryTobe;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StadiumServiceTobe {

	// TOOO : 공연장 생성 로직 없음

	private final StadiumRepositoryTobe stadiumRepository;

	public StadiumTobe findById(Long stadiumId) {
		return stadiumRepository.findById(stadiumId)
			.orElseThrow(() -> {
				throw new EntityNotFoundException("존재하지 않는 공연장입니다");
			});
	}

	public boolean isExistId(Long stadiumId) {
		return stadiumRepository.existsById(stadiumId);
	}
}
