package com.prgrms.be.intermark.auth;

import static com.prgrms.be.intermark.auth.constant.JwtConstants.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import javax.security.auth.login.AccountExpiredException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.prgrms.be.intermark.auth.dto.TokenResponseDTO;
import com.prgrms.be.intermark.common.dto.ErrorResponse;
import com.prgrms.be.intermark.domain.newerd.user.dto.UserIdAndRoleDTO;
import com.prgrms.be.intermark.util.CookieUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final CustomOauth2UserService customOauth2UserService;
	private final TokenProvider tokenProvider;
	private final TokenService tokenService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		if (authentication instanceof OAuth2AuthenticationToken) {

			// db에 유저정보 저장
			OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken)authentication;
			OAuth2User principal = oauth2Token.getPrincipal();
			String social = oauth2Token.getAuthorizedClientRegistrationId();
			ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

			log.info("{}", principal.getAttributes());
			try {
				//로그인
				UserIdAndRoleDTO userIdAndRoleDTO = customOauth2UserService.join(principal, social);
				String aceessToken = tokenProvider.createAceessToken(userIdAndRoleDTO.userId(),
					userIdAndRoleDTO.userRole());
				log.info(aceessToken);

				String refreshToken = tokenProvider.createRefreshToken(userIdAndRoleDTO.userId(),
					userIdAndRoleDTO.userRole());

				//refresh 갱신
				tokenService.assignRefreshToken(refreshToken);

				CookieUtil.deleteCookieByName(REFRESH_TOKEN_COOKIE_NAME, request, response);
				CookieUtil.addCookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken, REFRESH_TOKEN_COOKIE_MAX_AGE, response);

				String loginSuccessJson = mapper.writeValueAsString(generateLoginSuccessJson(aceessToken));
				response.setStatus(HttpStatus.OK.value());
				response.setContentType("application/json;charset=UTF-8");
				response.setContentLength(loginSuccessJson.getBytes(StandardCharsets.UTF_8).length);
				response.getWriter().write(loginSuccessJson);
				response.getWriter().flush();
				response.getWriter().close();
			} catch (AccountExpiredException e) {
				CookieUtil.deleteCookieByName(REFRESH_TOKEN_COOKIE_NAME, request, response);
				String deleteduserResponseBody = mapper.writeValueAsString(deletedUserErrorResponse(e));
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setContentType("application/json;charset=UTF-8");
				response.setContentLength(deleteduserResponseBody.getBytes(StandardCharsets.UTF_8).length);
				response.getWriter().write(deleteduserResponseBody);
				response.getWriter().flush();
				response.getWriter().close();
			}

		}

		super.onAuthenticationSuccess(request, response, authentication);
	}

	private ErrorResponse deletedUserErrorResponse(Exception e) {
		return ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getMessage(), LocalDateTime.now());
	}

	private TokenResponseDTO generateLoginSuccessJson(String accessToken) {
		return new TokenResponseDTO(accessToken);
	}
}
