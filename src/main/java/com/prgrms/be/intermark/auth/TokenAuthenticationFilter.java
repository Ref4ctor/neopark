package com.prgrms.be.intermark.auth;

import static com.prgrms.be.intermark.util.HeaderUtil.*;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.prgrms.be.intermark.domain.newerd.user.model.User;
import com.prgrms.be.intermark.domain.newerd.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

	private final TokenProvider tokenProvider;
	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// HTTP 요청에서 액세스 토큰을 가져옵니다.
		String accessToken = getAccessToken(request);

		// 토큰이 존재하고 유효한지 확인합니다.
		if (accessToken != null && tokenProvider.validate(accessToken)) {
			// 유효한 토큰인 경우, 인증 정보를 가져옵니다.
			Authentication authentication = tokenProvider.getAuthentication(accessToken);
			UserDetails userInToken = (UserDetails)authentication.getPrincipal();
			Long userIdInToken = Long.parseLong(userInToken.getUsername()); // 사용자 ID를 추출합니다.

			// DB에서 해당 사용자를 조회합니다.
			Optional<User> optionalUserInDB = userRepository.findByIdAndDeletedFalse(userIdInToken);
			if (optionalUserInDB.isPresent()) {
				User userInDB = optionalUserInDB.get();

				// DB의 사용자 ID와 토큰에서 추출한 사용자 ID가 일치하는지 확인합니다.
				if (userInDB.getId().equals(userIdInToken)) {
					SecurityContextHolder.getContext().setAuthentication(authentication);
				} else {
					// 사용자 ID가 일치하지 않으면 인증 실패 처리
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 사용자입니다.");
					return;
				}
			} else {
				// DB에서 사용자를 찾을 수 없으면 인증 실패 처리
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "사용자를 찾을 수 없습니다.");
				return;
			}
		}

		// 인증이 성공하거나 실패한 후, 다음 필터로 요청을 전달합니다.
		filterChain.doFilter(request, response);
	}
}
