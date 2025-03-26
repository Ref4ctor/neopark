package com.prgrms.be.intermark.domain.newerd.booking.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.prgrms.be.intermark.common.entity.BaseEntity;
import com.prgrms.be.intermark.domain.newerd.booking.dto.BookingHistoryResponse;
import com.prgrms.be.intermark.domain.newerd.user.model.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "booking_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BookingHistory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "seat_id", nullable = false)
	private Long seatId;

	@Column(name = "concert_schedule_id", nullable = false)
	private Long concertScheduleId;

	@Column(name = "concert_id", nullable = false)
	private Long concertId;

	@Column(name = "stadium_id", nullable = false)
	private Long stadiumId;

	@Column(name = "seat_grade_id", nullable = false)
	private Long seatGradeId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private BookingStatus status;

	public void cancel() {
		this.status = BookingStatus.CANCELLED;
	}

	public BookingHistoryResponse toBookingHistoryResponse(User user) {
		return BookingHistoryResponse.builder()
			.bookingStatus(status)
			.nickname(user.getNickname())
			.bookingId(id)
			.build();
	}

}
