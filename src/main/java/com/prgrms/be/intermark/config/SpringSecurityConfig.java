package com.prgrms.be.intermark.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.prgrms.be.intermark.auth.CookieOAuth2AuthorizationRequestRepository;
import com.prgrms.be.intermark.auth.CustomOauth2UserService;
import com.prgrms.be.intermark.auth.OAuth2AuthenticationSuccessHandler;
import com.prgrms.be.intermark.auth.OAuthAccessDeniedHandler;
import com.prgrms.be.intermark.auth.TokenAuthenticationFilter;
import com.prgrms.be.intermark.auth.TokenProvider;
import com.prgrms.be.intermark.auth.TokenService;
import com.prgrms.be.intermark.domain.newerd.user.model.UserRole;
import com.prgrms.be.intermark.domain.newerd.user.repository.UserRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig {
	private final CustomOauth2UserService customOauth2UserService;
	private final TokenProvider tokenProvider;
	@Value("${jwt.secret.access}")
	private String accessSecret;
	@Value("${jwt.secret.refresh}")
	private String refreshSecret;
	private final UserRepository userRepository;
	private final TokenService tokenService;

	public SpringSecurityConfig(CustomOauth2UserService customOauth2UserService, TokenProvider tokenProvider,
		UserRepository userRepository, TokenService tokenService) {
		this.customOauth2UserService = customOauth2UserService;
		this.tokenProvider = tokenProvider;
		this.userRepository = userRepository;
		this.tokenService = tokenService;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.headers()
			.frameOptions()
			.disable()
			.and()
			.csrf()
			.disable()
			.formLogin()
			.disable()
			.httpBasic()
			.disable()
			.exceptionHandling()
			//.accessDeniedHandler(oAuthAccessDeniedHandler())
			.and()
			.authorizeRequests()
			.antMatchers(
				"/swagger-ui.html",
				"/swagger-ui/**",
				"/v3/api-docs/**",
				"/swagger-resources/**",
				"/webjars/**",
				"/", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/login**", "/favicon.ico")
			.permitAll()
			.antMatchers("/api/v1/**")
			.hasAnyAuthority(UserRole.ROLE_USER.getKey(), UserRole.ROLE_ADMIN.getKey())
			.anyRequest()
			.authenticated()
			.and()
			.logout()
			.logoutSuccessUrl("/")
			.permitAll()
			.and()
			.oauth2Login()
			.authorizationEndpoint()
			.baseUri("/oauth2/authorization") //로그인페이지를 받기위한 서버의 엔드포인트 설정
			.authorizationRequestRepository(cookieOAuth2AuthorizationRequestRepository())
			.and()
			.redirectionEndpoint()
			.baseUri("/*/oauth2/code/*")
			.and()
			.userInfoEndpoint()
			.userService(customOauth2UserService)
			.and()
			.successHandler(oAuth2AuthenticationSuccessHandler(customOauth2UserService));
		return httpSecurity.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
			.build();
	}

	@Bean
	public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler(CustomOauth2UserService userService) {
		return new OAuth2AuthenticationSuccessHandler(userService, tokenProvider, tokenService);
	}

	@Bean
	public CookieOAuth2AuthorizationRequestRepository cookieOAuth2AuthorizationRequestRepository() {
		return new CookieOAuth2AuthorizationRequestRepository();
	}

	@Bean
	public OAuthAccessDeniedHandler oAuthAccessDeniedHandler() {
		return new OAuthAccessDeniedHandler();
	}

	@Bean
	public TokenAuthenticationFilter tokenAuthenticationFilter() {
		return new TokenAuthenticationFilter(tokenProvider, userRepository);
	}

}
