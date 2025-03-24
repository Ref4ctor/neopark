package com.prgrms.be.intermark.domain.newerd.concertschedule.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.be.intermark.domain.newerd.concertschedule.model.ConcertSchedule;

public interface ConcertScheduleRepository extends JpaRepository<ConcertSchedule, Long> {
	// @Query("SELECT COUNT(s) FROM Schedule s " +
	// 	"WHERE s.isDeleted = false AND s.startTime <= :endTime AND s.endTime >= :startTime " +
	// 	"AND s.musical.stadium = :stadium")*/
	// int getSchedulesNumByStartTime(
	// 	@Param("startTime") LocalDateTime startTime,
	// 	@Param("endTime") LocalDateTime endTime,
	// 	@Param("stadium") Stadium stadium
	// );
	//
	// @Query("SELECT COUNT(s) FROM Schedule s " +
	// 	"WHERE NOT s.id = :scheduleId AND s.startTime <= :endTime AND s.endTime >= :startTime " +
	// 	"AND s.isDeleted = false AND s.musical.stadium = :stadium")
	// int getDuplicatedScheduleExceptById(
	// 	@Param("scheduleId") Long scheduleId,
	// 	@Param("startTime") LocalDateTime startTime,
	// 	@Param("endTime") LocalDateTime endTime,
	// 	@Param("stadium") Stadium stadium
	// );
	//
	// boolean existsByMusicalAndIsDeletedFalse(Musical musical);
	//
	// Page<Schedule> findAllByMusical(Musical musical, Pageable pageable);
	//
	// List<Schedule> findByMusicalAndIsDeletedIsFalse(Musical musical);

	//List<Schedule> findByMusicalAndIsDeletedIsFalse(Musical musical);

	Optional<ConcertSchedule> findByIdAndStartTimeLessThan(Long id, LocalDateTime curTime);
}
