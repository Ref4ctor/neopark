package com.prgrms.be.intermark.domain.newerd.stadium.controller;

import java.net.URI;
import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.prgrms.be.intermark.common.exception.domain.stadium.DuplicatedStadiumException;
import com.prgrms.be.intermark.domain.newerd.stadium.dto.StadiumCreateRequest;
import com.prgrms.be.intermark.domain.newerd.stadium.dto.StadiumResponse;
import com.prgrms.be.intermark.domain.newerd.stadium.repository.StadiumRepositoryTobe;
import com.prgrms.be.intermark.domain.newerd.stadium.service.StadiumServiceTobe;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v2/stadiums")
public class StadiumControllerTobe {

	@Autowired
	private StadiumServiceTobe stadiumService;

	@Autowired
	private StadiumRepositoryTobe stadiumRepository;

	/**
	 * [FEAT] : 올바르지 않은 JSON 응답.. 이미 있어서 취소..
	 */
	// @ExceptionHandler(HttpMessageNotReadableException.class)
	// public ErrorResponse handleJsonParseError(HttpMessageNotReadableException ex) {
	// 	return ErrorResponse.of(
	// 		HttpStatus.BAD_REQUEST,
	// 		"입력값이 올바르지 않습니다. JSON 형식을 확인해주세요.",
	// 		LocalDateTime.now()
	// 	);
	// }

	/**
	 * [FEAT] : Request Validation 검증 실패 응답. 이미 있어서 취소..
	 */
	// @ResponseStatus(HttpStatus.BAD_REQUEST)
	// @ExceptionHandler(BindException.class)
	// public ErrorResponse invalidArgumentExHandler(BindException ex) {
	// 	log.error("[exceptionHandler] ex", ex);
	// 	return ErrorResponse.of(
	// 		HttpStatus.BAD_REQUEST,
	// 		ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
	// 		LocalDateTime.now()
	// 	);
	// }

	/**
	 * [FEAT] : 중복되는 데이터로 인한 예외 발생 응답.
	 */
	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(DuplicatedStadiumException.class)
	public ErrorResponse createStadiumDuplicatedExHandler(DuplicatedStadiumException ex) {
		log.error("[exceptionHandler] ex", ex);
		return ErrorResponse.of(
			HttpStatus.CONFLICT,
			ex.getMessage(),
			LocalDateTime.now());
	}

	@Autowired
	public StadiumControllerTobe(StadiumServiceTobe stadiumService) {
		this.stadiumService = stadiumService;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<Object> create(@RequestBody @Valid StadiumCreateRequest request) {

		// responseEntity 내부 
		StadiumResponse response = stadiumService.create(request.toServiceRequest());
		Long stadiumId = response.getId();

		return ResponseEntity.created(
				URI.create("/api/v2/stadiums/" + stadiumId))
			.build();
	}

	@GetMapping
	public ResponseDTO<Page<StadiumResponse>> getAllStadiums(Pageable pageable) {
		Page<StadiumResponse> stadiums = stadiumService.findAllStadiums(pageable);
		return ResponseDTO.success(stadiums);
	}

	@GetMapping("/{stadiumId}")
	public ResponseDTO<StadiumResponse> getStadium(@PathVariable("stadiumId") Long stadiumId) {
		StadiumResponse stadiumResponse = stadiumService.findStadiumById(stadiumId);
		return ResponseDTO.success(stadiumResponse);
	}

}
