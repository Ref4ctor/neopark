package com.prgrms.be.intermark.auth;

import static com.prgrms.be.intermark.auth.constant.JwtConstants.*;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TokenService {

	private final TokenProvider tokenProvider;
	private final UserRepository userRepository;

	public void assignRefreshToken(String refreshToken) {
		// TODO: 의존성 주입 받는 것 (빈 주입) vs 인자로 넣어주는 것 vs 안에서 만들어주는 것
		//refreshToken에서 id 뽑아옴
		String userIdFromRefreshToken = tokenProvider.getUserIdFromRefreshToken(refreshToken);
		Optional<User> user = userRepository.findById(Long.parseLong(userIdFromRefreshToken));
		//refresh갱신
		user.map(user1 -> {
			user1.setRefreshToken(refreshToken);

			return user1;
		}).orElseThrow(EntityNotFoundException::new);
	}

	public Optional<String> changeRefreshToken(Long userId, UserRole role, String refreshToken) {
		User findUser = userRepository.findByIdAndRefreshToken(userId, refreshToken)
			.orElseThrow(() -> new IllegalArgumentException("user id, refresh token으로 일치하는 유저를 찾을 수 없습니다."));
		String findUserRefreshToken = findUser.getRefreshToken();

		if (tokenProvider.getExpiration(findUserRefreshToken) <= THREE_DAYS_MSEC) {
			String newRefreshToken = tokenProvider.createRefreshToken(userId, role);

			findUser.setRefreshToken(newRefreshToken);
			return Optional.of(newRefreshToken);
		}
		return Optional.empty();
	}
}
