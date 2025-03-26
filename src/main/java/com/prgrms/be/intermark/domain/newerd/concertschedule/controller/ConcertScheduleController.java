package com.prgrms.be.intermark.domain.newerd.concertschedule.controller;

import java.net.URI;
import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.be.intermark.common.dto.ErrorResponse;
import com.prgrms.be.intermark.common.dto.ResponseDTO;
import com.prgrms.be.intermark.common.exception.domain.concertschedule.ConflictScheduleException;
import com.prgrms.be.intermark.common.exception.domain.concertschedule.InAvailableScheduleException;
import com.prgrms.be.intermark.domain.newerd.concert.dto.ConcertResponse;
import com.prgrms.be.intermark.domain.newerd.concert.service.ConcertService;
import com.prgrms.be.intermark.domain.newerd.concertschedule.dto.ConcertScheduleCreateRequest;
import com.prgrms.be.intermark.domain.newerd.concertschedule.dto.ConcertScheduleResponse;
import com.prgrms.be.intermark.domain.newerd.concertschedule.service.ConcertScheduleService;
import com.prgrms.be.intermark.domain.newerd.seatInfo.model.SeatInfoTobe;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v2/schedules")
@Slf4j
public class ConcertScheduleController {

	@Autowired
	private ConcertScheduleService concertScheduleService;

	@Autowired
	private ConcertService concertService;

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler()
	public ErrorResponse scheduleIsNotInConcertPeriod(InAvailableScheduleException ex) {
		log.error("[exceptionHandler] ex", ex);
		return ErrorResponse.of(
			HttpStatus.CONFLICT,
			ex.getMessage(),
			LocalDateTime.now()
		);
	}

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler()
	public ErrorResponse scheduleIsConflictedWithSameStadiumAndTime(ConflictScheduleException ex) {
		log.error("[exceptionHandler] ex", ex);
		return ErrorResponse.of(
			HttpStatus.CONFLICT,
			ex.getMessage(),
			LocalDateTime.now()
		);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<Object> createSchedule(@Valid @RequestBody ConcertScheduleCreateRequest request) {

		ConcertResponse concertResponse = concertService.findByConcertId(request.getConcertId());
		Long concertScheduleId = concertScheduleService.create(request.toServiceRequest(concertResponse));

		return ResponseEntity.created(URI.create("/api/v2/schedules/" + concertScheduleId)).build();
	}

	@GetMapping
	public ResponseDTO<Page<ConcertScheduleResponse>> getAllSchedules(Pageable pageable) {
		Page<ConcertScheduleResponse> allSchedules = concertScheduleService.findAllSchedules(pageable);
		return ResponseDTO.success(allSchedules);
	}

	@GetMapping("/{concertScheduleId}")
	public ResponseDTO<ConcertScheduleResponse> getConcertSchedule(
		@PathVariable("concertScheduleId") Long concertScheduleId) {
		ConcertScheduleResponse concertScheduleResponse =
			concertScheduleService.findConcertScheduleById(concertScheduleId);
		return ResponseDTO.success(concertScheduleResponse);
	}

	@GetMapping("/{concertScheduleId}/seats")
	public ResponseEntity<Page<SeatInfoTobe>> getSeatsByConcertSchedule(
		@PathVariable Long concertScheduleId,
		@PageableDefault(size = 20, sort = "id") Pageable pageable) {
		Page<SeatInfoTobe> seats = concertScheduleService.findAllByConcertScheduleId(concertScheduleId, pageable);
		return ResponseEntity.ok(seats);
	}
}
