package com.prgrms.be.intermark.domain.newerd.booking.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.prgrms.be.intermark.domain.newerd.booking.dto.BookingHistoryDTO;
import com.prgrms.be.intermark.domain.newerd.booking.model.BookingHistory;
import com.prgrms.be.intermark.domain.newerd.booking.model.BookingStatus;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface BookingHistoryRepository extends JpaRepository<BookingHistory, Long> {

	Optional<BookingHistory> findByIdAndUserIdAndStatus(Long bookingId, Long userId, BookingStatus status);

	@Query(value = """
		SELECT new com.prgrms.be.intermark.domain.newerd.booking.dto.BookingHistoryDTO(b.id, b.status, u, c, cs, s, st)
		FROM BookingHistory b
		JOIN FETCH User u ON b.userId = u.id
		JOIN FETCH ConcertTobe c ON b.concertId = c.id
		JOIN FETCH ConcertScheduleTobe  cs ON b.concertScheduleId = cs.id
		JOIN FETCH SeatInfoTobe s ON b.seatId = s.id
		JOIN FETCH StadiumTobe st ON b.stadiumId = st.id
		WHERE (:userId IS NULL OR b.userId = :userId)
		AND (:concertId IS NULL OR b.concertId = :concertId)
		""",
		countQuery = """
			SELECT COUNT(b)
			FROM BookingHistory b
			JOIN FETCH User u ON b.userId = u.id
			JOIN FETCH ConcertTobe c ON b.concertId = c.id
			JOIN FETCH ConcertScheduleTobe  cs ON b.concertScheduleId = cs.id
			JOIN FETCH SeatInfoTobe s ON b.seatId = s.id
			JOIN FETCH StadiumTobe st ON b.stadiumId = st.id
			WHERE (:userId IS NULL OR b.userId = :userId)
			AND (:concertId IS NULL OR b.concertId = :concertId)
			""")
	Page<BookingHistoryDTO> findBookingHistoryByCondition(@Param("userId") Long userId,
		@Param("concertId") Long concertId, Pageable pageable);

	@Query(value = """
		SELECT new com.prgrms.be.intermark.domain.newerd.booking.dto.BookingHistoryDTO(b.id, b.status, u, c, cs, s, st)
		FROM BookingHistory b
		JOIN FETCH User u ON b.userId = u.id
		JOIN FETCH ConcertTobe c ON b.concertId = c.id
		JOIN FETCH ConcertScheduleTobe  cs ON b.concertScheduleId = cs.id
		JOIN FETCH SeatInfoTobe s ON b.seatId = s.id
		JOIN FETCH StadiumTobe st ON b.stadiumId = st.id
		WHERE b.id = :bookingId
		""")
	Optional<BookingHistoryDTO> findDTOById(@Param("bookingId") Long bookingId);

}
