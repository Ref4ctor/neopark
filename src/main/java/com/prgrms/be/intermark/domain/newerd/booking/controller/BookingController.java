package com.prgrms.be.intermark.domain.newerd.booking.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgrms.be.intermark.common.dto.ResponseDTO;
import com.prgrms.be.intermark.domain.newerd.booking.dto.BookingHistoryCondition;
import com.prgrms.be.intermark.domain.newerd.booking.dto.BookingHistoryResponse;
import com.prgrms.be.intermark.domain.newerd.booking.dto.ReserveConcertRequest;
import com.prgrms.be.intermark.domain.newerd.booking.service.BookingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/api/v2/bookings")
@RequiredArgsConstructor
@RestController
public class BookingController {

	private final BookingService bookingService;

	@PostMapping
	public ResponseEntity<ResponseDTO<?>> reserveConcert(
		@RequestBody @Valid ReserveConcertRequest reserveConcertRequest) {
		Long bookingId = bookingService.reserveConcert_Locking(reserveConcertRequest);
		return ResponseEntity.created(
			URI.create("/api/v2/bookings/" + bookingId)
		).body(ResponseDTO.success());
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<ResponseDTO<?>> cancelConcert(@AuthenticationPrincipal User user,
		@PathVariable Long bookingId) {
		bookingService.cancelConcert(Long.valueOf(user.getUsername()), bookingId);
		return ResponseEntity.ok().body(ResponseDTO.success());
	}

	@GetMapping()
	public ResponseEntity<ResponseDTO<Page<BookingHistoryResponse>>> getAllBookingHistory(
		BookingHistoryCondition bookingHistoryCondition, Pageable pageable) {
		Page<BookingHistoryResponse> page = bookingService.getBookingHistoryPage(bookingHistoryCondition, pageable);
		return ResponseEntity.ok().body(ResponseDTO.success(page));
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<ResponseDTO<BookingHistoryResponse>> getBookingHistory(@PathVariable Long bookingId) {
		BookingHistoryResponse bookingHistoryResponse = bookingService.getBookingHistory(bookingId);
		return ResponseEntity.ok().body(ResponseDTO.success(bookingHistoryResponse));
	}

}
