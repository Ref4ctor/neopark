package com.prgrms.be.intermark.domain.newerd.concertschedule.service;

import java.time.LocalDateTime;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import com.prgrms.be.intermark.domain.newerd.concertschedule.model.ConcertSchedule;
import com.prgrms.be.intermark.domain.newerd.concertschedule.repository.ConcertScheduleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConcertScheduleValidationService {

	private final ConcertScheduleRepository concertScheduleRepository;

	public ConcertSchedule findAvailableConcertSchedule(Long scheduleId) {
		return concertScheduleRepository.findByIdAndStartTimeLessThan(scheduleId, LocalDateTime.now())
			.orElseThrow(() -> new EntityNotFoundException("존재하지 않거나 이미 지난 스케줄입니다"));
	}
}
