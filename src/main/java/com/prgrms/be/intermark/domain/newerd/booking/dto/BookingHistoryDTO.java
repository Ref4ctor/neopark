package com.prgrms.be.intermark.domain.newerd.booking.dto;

import com.prgrms.be.intermark.domain.newerd.booking.model.BookingStatus;
import com.prgrms.be.intermark.domain.newerd.concert.model.ConcertTobe;
import com.prgrms.be.intermark.domain.newerd.concertschedule.model.ConcertScheduleTobe;
import com.prgrms.be.intermark.domain.newerd.seatInfo.model.SeatInfoTobe;
import com.prgrms.be.intermark.domain.newerd.stadium.model.StadiumTobe;
import com.prgrms.be.intermark.domain.newerd.user.model.User;

public record BookingHistoryDTO(
	Long bookingId,
	BookingStatus bookingStatus,
	User user,
	ConcertTobe concert,
	ConcertScheduleTobe concertScheduleTobe,
	SeatInfoTobe seatInfo,
	StadiumTobe stadium
) {

	public BookingHistoryResponse toBookingHistoryResponse() {
		return BookingHistoryResponse.builder()
			.concertResponse(concert.createResponse())
			.stadiumResponse(stadium.createResponse())
			.bookingId(bookingId)
			.nickname(user.getNickname())
			.bookingStatus(bookingStatus)
			.seatInfoResponse(seatInfo.toResponse())
			.concertScheduleResponse(concertScheduleTobe.createResponse())
			.build();
	}
}
