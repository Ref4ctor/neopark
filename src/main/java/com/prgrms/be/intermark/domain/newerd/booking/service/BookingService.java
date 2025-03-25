package com.prgrms.be.intermark.domain.newerd.booking.service;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.prgrms.be.intermark.domain.newerd.booking.dto.BookingHistoryCondition;
import com.prgrms.be.intermark.domain.newerd.booking.dto.BookingHistoryResponse;
import com.prgrms.be.intermark.domain.newerd.booking.dto.ReserveConcertRequest;
import com.prgrms.be.intermark.domain.newerd.booking.model.BookingHistory;
import com.prgrms.be.intermark.domain.newerd.booking.repository.BookingHistoryRepository;
import com.prgrms.be.intermark.domain.newerd.concertschedule.service.ConcertScheduleValidationService;
import com.prgrms.be.intermark.domain.newerd.seatInfo.model.SeatInfoTobe;
import com.prgrms.be.intermark.domain.newerd.seatInfo.service.SeatInfoValidationService;
import com.prgrms.be.intermark.domain.newerd.user.model.UserTobe;
import com.prgrms.be.intermark.domain.newerd.user.service.UserValidationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

	private final UserValidationService userValidationService;
	private final SeatInfoValidationService seatInfoValidationService;
	private final ConcertScheduleValidationService concertScheduleValidationService;
	private final BookingHistoryRepository bookingHistoryRepository;
	private final BookingValidationService bookingValidationService;

	private final RedissonClient redissonClient;

	//TODO 토큰에서 인증하는걸로 바꿔서 user검증로직은 지울예정
	@Transactional
	public Long reserveConcert_Locking(ReserveConcertRequest reserveConcertRequest) {
		// (1) 사용자 검증 (사용자가 존재하고 활성화 상태인지 확인 후 예외처리)
		// TODO: 사용자는 현재 존재하지 않으니 주석 처리 (시연할 때, 구지 필요 없으면 주석 처리)
		//userValidationService.findActiveUser(reserveConcertRequest.userId());

		SeatInfoTobe seatInfoTobe = seatInfoValidationService.findAvailableSeatInfo(reserveConcertRequest.seatId());

		// (3) 공연 일정 상태 조회 (예약 가능한 공연인지 확인 후 예외처리)
		// TODO: 공연 일정은 현재 존재하지 않으니 주석 처리 (시연할 때, 구지 필요 없으면 주석 처리)
		//concertScheduleValidationService.findAvailableConcertSchedule(seatInfoTobe.getConcertScheduleId());

		seatInfoTobe.reserve();
		return bookingHistoryRepository.save(reserveConcertRequest.toBookingHistory(seatInfoTobe)).getId();
	}

	@Transactional
	public Long reserveConcert_redisson(ReserveConcertRequest reserveConcertRequest) {
		// seatId를 기반으로 고유한 락 키 생성
		RLock rLock = redissonClient.getLock(reserveConcertRequest.getSeatId().toString());
		rLock.lock();
		try {

			// (1) 사용자 검증 (사용자가 존재하고 활성화 상태인지 확인 후 예외처리)
			// TODO: 사용자는 현재 존재하지 않으니 주석 처리 (시연할 때, 구지 필요 없으면 주석 처리)
			//userValidationService.findActiveUser(reserveConcertRequest.userId());

			// (2) 좌석 상태 조회 (예약 가능한 좌석인지 확인 후 예외처리)
			SeatInfoTobe seatInfoTobe = seatInfoValidationService.findAvailableSeatInfo(
				reserveConcertRequest.getSeatId());

			// (3) 공연 일정 상태 조회 (예약 가능한 공연인지 확인 후 예외처리)
			// TODO: 공연 일정은 현재 존재하지 않으니 주석 처리 (시연할 때, 구지 필요 없으면 주석 처리)
			//concertScheduleValidationService.findAvailableConcertSchedule(seatInfoTobe.getConcertScheduleId());

			if (seatInfoTobe.getReserved()) {
				throw new IllegalStateException("이미 예약된 좌석입니다.");
			} else {
				// (1) 좌석 예매
				seatInfoTobe.reserve();

				// (2) 예매 내역 저장
				BookingHistory bookingHistory = bookingHistoryRepository
					.save(reserveConcertRequest.toBookingHistory(seatInfoTobe));

				log.info("예매가 완료되었습니다. bookingId : {}", bookingHistory.getId());

				// (3) 트랜잭션 커밋 시, 락 해제
				TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
					@Override
					public void afterCommit() {
						rLock.unlock();
					}
				});

				return bookingHistory.getId();
			}
		} catch (Exception e) {
			// 예외 발생 시, 락이 해제되지 않았으면 해제
			if (rLock.isHeldByCurrentThread()) {
				rLock.unlock();
			}
			throw e;
		}
	}

	@Transactional
	public void cancelConcert(Long userId, Long historyId) {
		BookingHistory bookingHistory = bookingValidationService.findBookingHistory(historyId, userId);
		SeatInfoTobe seatInfoTobe = seatInfoValidationService.findReservedSeatInfo(bookingHistory.getSeatId());
		seatInfoTobe.cancel();
		bookingHistory.cancel();
	}

	//TODO 코드 합치고 response 더 추가해야함
	public Page<BookingHistoryResponse> getBookingHistoryPage(BookingHistoryCondition bookingHistoryCondition,
		Pageable pageable) {
		Page<BookingHistory> bookingHistoryPage = bookingHistoryRepository.findAllByUserIdAndConcertId(
			bookingHistoryCondition.userId(), bookingHistoryCondition.concertId(), pageable);
		return bookingHistoryPage.map(b -> {
			UserTobe user = userValidationService.findUser(b.getUserId());
			return b.toBookingHistoryResponse(user);
		});
	}

	public BookingHistoryResponse getBookingHistory(Long bookingId) {
		BookingHistory bookingHistory = bookingValidationService.findById(bookingId);
		UserTobe user = userValidationService.findUser(bookingHistory.getUserId());
		return bookingHistory.toBookingHistoryResponse(user);
	}

}
