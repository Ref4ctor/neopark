package com.prgrms.be.intermark.domain.newerd.concertschedule.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prgrms.be.intermark.domain.newerd.concertschedule.model.ConcertSchedule;

public interface ConcertScheduleRepository extends JpaRepository<ConcertSchedule, Long> {

	@Query("""
		    SELECT COUNT(s) > 0 
			FROM ConcertSchedule s
			WHERE 1=1  
				AND s.deleted = false
				AND s.startTime <= :endTime
			    AND s.endTime >= :startTime
				AND s.stadiumId = :stadiumId
		""")
	boolean isConflictScheduleExist(
		@Param("startTime") LocalDateTime startTime,
		@Param("endTime") LocalDateTime endTime,
		@Param("stadiumId") Long stadiumId
	);

	Optional<ConcertSchedule> findByIdAndStartTimeLessThan(Long id, LocalDateTime curTime);
}
