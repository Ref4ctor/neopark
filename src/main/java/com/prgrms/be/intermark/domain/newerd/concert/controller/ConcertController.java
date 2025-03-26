package com.prgrms.be.intermark.domain.newerd.concert.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prgrms.be.intermark.common.dto.ErrorResponse;
import com.prgrms.be.intermark.common.dto.ResponseDTO;
import com.prgrms.be.intermark.common.exception.domain.concert.DuplicatedConcertException;
import com.prgrms.be.intermark.domain.newerd.concert.dto.ConcertCreateRequest;
import com.prgrms.be.intermark.domain.newerd.concert.dto.ConcertResponse;
import com.prgrms.be.intermark.domain.newerd.concert.service.ConcertService;
import com.prgrms.be.intermark.domain.newerd.concertschedule.dto.ConcertScheduleResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v2/concerts")
public class ConcertController {

	@Autowired
	private final ConcertService concertService;

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(DuplicatedConcertException.class)
	public ErrorResponse createConcertDuplicatedExHandler(DuplicatedConcertException ex) {
		log.error("[exceptionHandler] ex", ex);
		return ErrorResponse.of(
			HttpStatus.CONFLICT,
			ex.getMessage(),
			LocalDateTime.now());
	}

	public ConcertController(ConcertService concertService) {
		this.concertService = concertService;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Object> createConcert(
		@RequestPart(value = "concertCreateRequest") @Valid ConcertCreateRequest concertCreateRequest,
		@RequestPart(value = "thumbnail") MultipartFile thumbnail,
		@RequestPart(value = "detailImages") List<MultipartFile> detailImages
	) {
		Long concertId = concertService.create(concertCreateRequest.toServiceRequest(), thumbnail, detailImages);
		return ResponseEntity.created(
				URI.create("/api/v2/concerts/" + concertId))
			.build();
	}

	@GetMapping
	public ResponseDTO<Page<ConcertResponse>> getAllConcerts(Pageable pageable) {
		Page<ConcertResponse> concertResponses = concertService.findAllPages(pageable);
		return ResponseDTO.success(concertResponses);
	}

	@GetMapping("/{concertId}")
	public ResponseDTO<ConcertResponse> getMusical(@PathVariable("concertId") Long concertId) {
		ConcertResponse concertResponse = concertService.findByConcertId(concertId);
		return ResponseDTO.success(concertResponse);
	}

	@GetMapping("/{concertId}/schedules")
	public ResponseDTO<Page<ConcertScheduleResponse>> getSchedules(
		@PathVariable("concertId") Long concertId, Pageable pageable) {
		Page<ConcertScheduleResponse> schedulesByConcertId = concertService.findSchedulesByConcertId(concertId,
			pageable);

		return ResponseDTO.success(schedulesByConcertId);
	}
}
