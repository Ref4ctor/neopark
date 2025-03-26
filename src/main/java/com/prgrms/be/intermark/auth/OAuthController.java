package com.prgrms.be.intermark.auth;

import static com.prgrms.be.intermark.auth.constant.JwtConstants.*;
import static com.prgrms.be.intermark.util.HeaderUtil.*;

import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.prgrms.be.intermark.auth.dto.TokenResponseDTO;
import com.prgrms.be.intermark.domain.newerd.user.model.UserRole;
import com.prgrms.be.intermark.util.CookieUtil;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OAuthController {

	private final TokenProvider tokenProvider;
	private final TokenService tokenService;

	@GetMapping("/refresh")
	public ResponseEntity<Object> tokenRefresh(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = getAccessToken(request);

		if (!tokenProvider.validate(accessToken)) {
			// 예외를 던지면 GlobalExceptionHandler에서 처리됨
			throw new IllegalArgumentException("access token이 validate 하지 않습니다.");
		}

		Claims claims = tokenProvider.getExpiredTokenClaims(accessToken);
		if (Objects.isNull(claims)) {
			return ResponseEntity.ok(new TokenResponseDTO(accessToken));
		}

		Long userId = Long.parseLong(claims.getSubject());
		UserRole role = UserRole.valueOf(claims.get("role", String.class));
		String refreshToken = CookieUtil.getCookieByName(REFRESH_TOKEN_COOKIE_NAME, request)
			.map(Cookie::getValue)
			.orElse(null);

		if (!tokenProvider.validate(refreshToken)) { // refresh 토큰이 유효함.
			throw new IllegalArgumentException("refresh token이 validate 하지 않습니다.");
		}

		Optional<String> newRefreshToken = tokenService.changeRefreshToken(userId, role, refreshToken);
		if (newRefreshToken.isPresent()) {
			CookieUtil.deleteCookieByName(REFRESH_TOKEN_COOKIE_NAME, request, response);
			CookieUtil.addCookie(REFRESH_TOKEN_COOKIE_NAME, newRefreshToken.get(), REFRESH_TOKEN_COOKIE_MAX_AGE,
				response);
		}

		String newAccessToken = tokenProvider.createAceessToken(userId, role);
		return ResponseEntity.ok(new TokenResponseDTO(newAccessToken));
	}
}
