package com.prgrms.be.intermark.domain.newerd.booking.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.prgrms.be.intermark.domain.newerd.booking.model.BookingHistory;
import com.prgrms.be.intermark.domain.newerd.booking.model.BookingStatus;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface BookingHistoryRepository extends JpaRepository<BookingHistory, Long> {

	Optional<BookingHistory> findByIdAndUserIdAndStatus(Long bookingId, Long userId, BookingStatus status);

	@Query(value = """
		SELECT b
		FROM BookingHistory b
		WHERE (:userId IS NULL OR b.userId = :userId)
		  AND (:concertId IS NULL OR b.concertId = :concertId)
		""",
		countQuery = """
			SELECT COUNT(b)
			FROM BookingHistory b
			WHERE (:userId IS NULL OR b.userId = :userId)
			  AND (:concertId IS NULL OR b.concertId = :concertId)
			""")
	Page<BookingHistory> findAllByUserIdAndConcertId(@Param("userId") Long userId,
		@Param("concertId") Long concertId, Pageable pageable);
}
