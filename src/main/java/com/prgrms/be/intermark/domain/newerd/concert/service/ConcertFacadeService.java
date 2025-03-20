package com.prgrms.be.intermark.domain.newerd.concert.service;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.prgrms.be.intermark.common.dto.ImageResponseDTO;
import com.prgrms.be.intermark.common.dto.page.PageListIndexSize;
import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.common.service.ImageUploadService;
import com.prgrms.be.intermark.domain.casting.service.CastingService;
import com.prgrms.be.intermark.domain.musical.dto.MusicalDetailResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalSummaryResponseDTO;
import com.prgrms.be.intermark.domain.musical.dto.MusicalUpdateRequestDTO;
import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.service.MusicalDetailImageService;
import com.prgrms.be.intermark.domain.musical.service.MusicalService;
import com.prgrms.be.intermark.domain.musical_seat.service.MusicalSeatService;
import com.prgrms.be.intermark.domain.newerd.actor.model.ActorTobe;
import com.prgrms.be.intermark.domain.newerd.actor.service.ActorServiceTobe;
import com.prgrms.be.intermark.domain.newerd.castinginfo.model.CastingInfo;
import com.prgrms.be.intermark.domain.newerd.castinginfo.repository.CastingInfoRepositoryTobe;
import com.prgrms.be.intermark.domain.newerd.concert.dto.ConcertCreateRequestDTO;
import com.prgrms.be.intermark.domain.newerd.concert.model.Concert;
import com.prgrms.be.intermark.domain.newerd.concert.model.ConcertDetailImage;
import com.prgrms.be.intermark.domain.schedule.service.ScheduleService;
import com.prgrms.be.intermark.domain.seat.service.SeatService;
import com.prgrms.be.intermark.domain.seatgrade.service.SeatGradeService;
import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.stadium.service.StadiumService;
import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import com.prgrms.be.intermark.domain.ticket.service.TicketService;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConcertFacadeService {

	private static final String THUMBNAIL_PATH = "concert/thumbnail/";
	private static final String DETAIL_IMAGES_PATH = "concert/detailImages/";

	private final MusicalService musicalService;
	private final SeatGradeService seatGradeService;
	private final ImageUploadService imageUploadService;
	private final MusicalDetailImageService musicalDetailImageService;
	private final MusicalSeatService musicalSeatService;
	private final CastingService castingService;
	private final TicketService ticketService;
	private final ScheduleService scheduleService;

	private final SeatService seatService;

	private final ActorServiceTobe actorService;
	private final StadiumService stadiumService;
	private final UserService userService;
	private final ConcertService concertService;
	private final ConcertDetailImageService concertDetailImageService;
	private final CastingInfoRepositoryTobe castingInfoRepository;

	@Transactional
	public Long create(
		ConcertCreateRequestDTO concertCreateRequestDTO,
		MultipartFile thumbnail,
		List<MultipartFile> detailImages
	) {

		// TODO : 전체적인 기능 검증 필요, 예외처리 필요.

		// 공연장이 존재하는지 확인
		boolean isExistStadium = stadiumService.isExistId(concertCreateRequestDTO.stadiumId());
		if (!isExistStadium) {
			throw new EntityNotFoundException("해당 아이디를 가진 공연장이 존재하지 않습니다.");
		}

		// 공연 등록하는 유저가 존재하는지 확인
		boolean isExistUser = userService.existsById(concertCreateRequestDTO.managerId());
		if (!isExistUser) {
			throw new EntityNotFoundException("해당 아이디를 가진 유저가 존재하지 않습니다.");
		}

		// 공연 썸네일 이미지 생성 및 그 경로를 반환을 위한 메서드
		// thumbnail MultipartFile 을 THUMBNAIL_PATH 하위에 저장,
		// 그 후 원본파일이름과 파일저장경로 반환
		ImageResponseDTO thumbnailInfoDto = imageUploadService.uploadImage(thumbnail, THUMBNAIL_PATH);

		// 공연을 만들어야, 공연 일정을 잡을 수 있음.
		Concert concert = Concert.fromDto(concertCreateRequestDTO, thumbnailInfoDto);

		// List<MultipartFile> detailImages 로 전달된 데이터로 공연 상세 이미지 저장, 이후 그 정보를 담은 리스트 반환
		List<ImageResponseDTO> detailImagesInfo = imageUploadService.uploadImages(detailImages, DETAIL_IMAGES_PATH);

		// 콘서트 엔티티 저장
		Long concertId = concertService.save(concert);

		// 콘서트 상세 이미지
		// 이미지 하나하나와 콘서트 아이디를 연결해서 저장필요
		List<ConcertDetailImage> concertDetailImages =
			ConcertDetailImage.fromImagesAndConcertId(detailImagesInfo,
				concertId);

		// 콘서트 상세 이미지 저장
		concertDetailImageService.saveDetailImages(concertDetailImages);

		// ConcertCreateRequestDTO 내부의 배우 목록으로, CastingService 에서 ActorRepository, CastingInfoRepository 에 저장

		// TODO : 공연, 배우 관련 로직 수정 필요. 메서드로 묶거나, 별도 서비스에서 처리하도록
		// TODO : 기존 시스템에서는 배우가 있다고 가정함, 배우 테이블에 배우가 있다면 가져오고, 없다면 저장하는 방식으로 진행 필요할듯.
		// 사실 배우 목록 전달할때 이름으로 하는게 맞지 않나 싶은 생각이 있긴 함, 공연 출연자 등록하는데 사용자가 배우 id를 어떻게 알아..
		// 공연 배우 목록 전달할때, 배우명, 배우고유id 넘겨주면, 우리 DB에 있는지 확인하고 없다면 저장해야 할듯.
		// 그 후에 아래 로직처럼 배우 리스트 가져와서 출연배우 테이블에 저장하는 방식으로 구현하면 어떨까 싶음.

		// TODO : 따라서, 배우가 존재하지 않는 경우 배우 테이블에 저장하는 로직 필요..
		// ex) actorService.saveActorIfNonExist(배우목록DTO)

		// Actor 테이블에서 목록 가져와서 CastingInfo 테이블에 저장
		List<CastingInfo> castingInfoList = concertCreateRequestDTO.actorIds()
			.stream()
			.map(actorId -> {
				ActorTobe actor = actorService.findById(actorId);
				return CastingInfo.builder()
					.actorId(actor.getId())
					.concertId(concertId)
					.build();
			})
			.toList();

		// CastingInfoRepository 에 저장
		castingInfoRepository.saveAll(castingInfoList);

		return concertId;

	}

	@Transactional
	public void update(
		Long musicalId,
		MusicalUpdateRequestDTO musicalUpdateRequestDTO,
		MultipartFile thumbnailImage,
		List<MultipartFile> detailImages
	) {
		Musical musical = musicalService.findMusicalById(musicalId);

		if (scheduleService.existsByMusical(musical)) {
			throw new IllegalArgumentException("이미 뮤지컬의 스케줄이 존재합니다.");
		}

		if (ticketService.existsByMusical(musical)) {
			throw new IllegalArgumentException("이미 예약된 뮤지컬입니다.");
		}

		ImageResponseDTO thumbnailInfo = imageUploadService.uploadImage(thumbnailImage, THUMBNAIL_PATH);
		Stadium stadium = stadiumService.findById(musicalUpdateRequestDTO.stadiumId());
		User manager = userService.findByIdForFacade(musicalUpdateRequestDTO.managerId());

		seatGradeService.update(musicalUpdateRequestDTO.seatGrades(), musical);
		musicalSeatService.update(musicalUpdateRequestDTO.seats(), musicalUpdateRequestDTO.stadiumId(), musical);
		castingService.update(musicalUpdateRequestDTO.actors(), musical);

		List<ImageResponseDTO> detailImagesInfo = imageUploadService.uploadImages(detailImages, DETAIL_IMAGES_PATH);
		musicalDetailImageService.update(detailImagesInfo, musical);
		musicalService.updateMusical(musical, musicalUpdateRequestDTO, thumbnailInfo.path(), stadium, manager);
	}

	@Transactional(readOnly = true)
	public PageResponseDTO<Musical, MusicalSummaryResponseDTO> findAllMusicals(Pageable pageable) {
		Page<Musical> musicalPage = musicalService.findAllMusicals(pageable);
		return new PageResponseDTO<>(musicalPage, MusicalSummaryResponseDTO::from,
			PageListIndexSize.MUSICAL_LIST_INDEX_SIZE);
	}

	@Transactional(readOnly = true)
	public MusicalDetailResponseDTO findMusicalById(Long musicalId) {
		Musical musical = musicalService.findMusicalById(musicalId);
		return MusicalDetailResponseDTO.from(musical);
	}

	@Transactional
	public void deleteMusical(Long musicalId) {
		Musical musical = musicalService.findMusicalById(musicalId);

		boolean hasReservedTicket = musical.getTickets()
			.stream()
			.anyMatch(Ticket::isReserved);

		if (hasReservedTicket) {
			throw new RuntimeException("예매된 티켓이 있어 뮤지컬을 삭제할 수 없습니다");
		}

		musicalService.deleteMusical(musical);
		castingService.deleteAllByMusical(musical);
		musicalDetailImageService.deleteAllByMusical(musical);
		scheduleService.deleteAllByMusical(musical);
		seatGradeService.deleteAllByMusical(musical);
		musicalSeatService.deleteAllByMusical(musical);
	}
}
