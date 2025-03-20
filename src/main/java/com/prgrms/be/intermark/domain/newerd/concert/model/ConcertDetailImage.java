package com.prgrms.be.intermark.domain.newerd.concert.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.prgrms.be.intermark.common.dto.ImageResponseDTO;
import com.prgrms.be.intermark.common.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "concert_detail_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ConcertDetailImage extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long concertId;

	@NotBlank
	@Column(name = "original_file_name", nullable = false)
	private String originalFileName;

	@NotBlank
	@Column(name = "image_path", nullable = false, length = 2000)
	private String imagePath;

	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted;

	@Builder
	private ConcertDetailImage(Long concertId, String originalFileName, String imagePath) {
		this.concertId = concertId;
		this.originalFileName = originalFileName;
		this.imagePath = imagePath;
		this.isDeleted = false;
	}

	public static List<ConcertDetailImage> fromImagesAndConcertId(
		List<ImageResponseDTO> detailImages, long concertId) {
		return detailImages.stream()
			.map(imageResponse -> ConcertDetailImage.builder()
				.concertId(concertId)
				.originalFileName(imageResponse.originalFileName())
				.imagePath(imageResponse.path())
				.build()
			).toList();
	}

	public void deleteMusicalDetailImage() {
		this.isDeleted = true;
	}

}
