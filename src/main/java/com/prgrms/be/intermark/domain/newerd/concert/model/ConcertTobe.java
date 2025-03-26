package com.prgrms.be.intermark.domain.newerd.concert.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.prgrms.be.intermark.common.entity.BaseEntity;
import com.prgrms.be.intermark.domain.newerd.concert.dto.ConcertCreateServiceRequest;
import com.prgrms.be.intermark.domain.newerd.concert.dto.ConcertResponse;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "concert")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ConcertTobe extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "title", nullable = false)
	private String title;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "view_rating", nullable = false, length = 10)
	private ViewRating viewRating;

	@Column(name = "thumbnail_path", nullable = false)
	private String thumbnailPath;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "genre", nullable = false, length = 20)
	private Genre genre;

	@Lob
	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@Column(name = "end_date", nullable = false)
	private LocalDate endDate;

	@Column(name = "running_time", nullable = false)
	private int runningTime;

	// 보류
	@Column(name = "is_deleted", nullable = false)
	private boolean deleted;

	@Builder
	private ConcertTobe(
		String title, ViewRating viewRating, Genre genre,
		String description, String thumbnailPath, LocalDate startDate, LocalDate endDate,
		int runningTime, Long userId) {
		this.title = title;
		this.viewRating = viewRating;
		this.genre = genre;
		this.thumbnailPath = thumbnailPath;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.runningTime = runningTime;
		this.deleted = false;
		this.userId = userId;
	}

	public static ConcertTobe createWithThumbnailPath(ConcertCreateServiceRequest createRequestDto,
		String thumbnailPath) {
		return ConcertTobe.builder()
			.title(createRequestDto.title())
			.thumbnailPath(thumbnailPath)
			.viewRating(createRequestDto.viewRating())
			.genre(createRequestDto.genre())
			.description(createRequestDto.description())
			.startDate(createRequestDto.startDate())
			.endDate(createRequestDto.endDate())
			.userId(createRequestDto.managerId())
			.runningTime(createRequestDto.runningTime())
			.build();
	}

	public static ConcertTobe create(ConcertCreateServiceRequest createRequestDto) {
		return ConcertTobe.builder()
			.title(createRequestDto.title())
			.viewRating(createRequestDto.viewRating())
			.genre(createRequestDto.genre())
			.description(createRequestDto.description())
			.startDate(createRequestDto.startDate())
			.endDate(createRequestDto.endDate())
			.userId(createRequestDto.managerId())
			.runningTime(createRequestDto.runningTime())
			.build();
	}

	public ConcertResponse createResponse() {
		return ConcertResponse.builder()
			.id(id)
			.title(title)
			.viewRating(viewRating)
			.genre(genre)
			.description(description)
			.startDate(startDate)
			.endDate(endDate)
			.managerId(userId)
			.runningTime(runningTime)
			.build();
	}

	public void deleteMusical() {
		this.deleted = true;
	}
}
