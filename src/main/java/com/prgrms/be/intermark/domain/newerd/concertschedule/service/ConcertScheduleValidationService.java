package com.prgrms.be.intermark.domain.newerd.concertschedule.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.be.intermark.common.exception.domain.concertschedule.ConflictScheduleException;
import com.prgrms.be.intermark.common.exception.domain.concertschedule.InAvailableScheduleException;
import com.prgrms.be.intermark.domain.newerd.concert.dto.ConcertResponse;
import com.prgrms.be.intermark.domain.newerd.concertschedule.dto.ConcertScheduleCreateServiceRequest;
import com.prgrms.be.intermark.domain.newerd.concertschedule.model.ConcertSchedule;
import com.prgrms.be.intermark.domain.newerd.concertschedule.repository.ConcertScheduleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertScheduleValidationService {

	private final ConcertScheduleRepository concertScheduleRepository;

	public void checkConcertScheduleCreateRequestIsPossible(ConcertScheduleCreateServiceRequest request) {
		boolean isConflictScheduleExist = concertScheduleRepository.isConflictScheduleExist(
			request.getStartTime(),
			request.getEndTime(),
			request.getStadiumId());

		if (isConflictScheduleExist) {
			throw new ConflictScheduleException(request.getStartTime(), request.getEndTime());
		}
	}

	public void checkScheduleIsInConcertPeriod(ConcertScheduleCreateServiceRequest request, ConcertResponse concert) {
		boolean isAvailableSchedule = isScheduleInConcertPeriod(concert, request);
		if (!isAvailableSchedule) {
			throw new InAvailableScheduleException(concert, request);
		}
	}

	public boolean isScheduleInConcertPeriod(ConcertResponse concert, ConcertScheduleCreateServiceRequest schedule) {
		LocalDate scheduleStartDate = schedule.getStartTime().toLocalDate();
		LocalDate scheduleEndDate = schedule.getEndTime().toLocalDate();

		return !scheduleStartDate.isBefore(concert.getStartDate())
			&& !scheduleEndDate.isAfter(concert.getEndDate());
	}

	public ConcertSchedule findAvailableConcertSchedule(Long scheduleId) {
		return concertScheduleRepository.findByIdAndStartTimeLessThan(scheduleId, LocalDateTime.now())
			.orElseThrow(() -> new EntityNotFoundException("존재하지 않거나 이미 지난 스케줄입니다"));
	}

	public ConcertSchedule findById(Long concertScheduleId) {
		return concertScheduleRepository.findById(concertScheduleId)
			.orElseThrow(() -> new EntityNotFoundException("해당 아이디에 대한 공연 일정이 존재하지 않습니다."));
	}
}
