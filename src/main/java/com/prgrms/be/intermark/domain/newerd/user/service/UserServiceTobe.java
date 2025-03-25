package com.prgrms.be.intermark.domain.newerd.user.service;

import javax.security.auth.login.AccountExpiredException;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.be.intermark.auth.OAuthAttribute;
import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.dto.UserIdAndRoleDTO;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceTobe {

	private final UserValidationService userValidationService;
	private final UserRepository userRepository;

	@Transactional
	public UserIdAndRoleDTO join(OAuth2User oauth2User, String social) throws AccountExpiredException {
		SocialType socialType = SocialType.valueOf(social.toUpperCase());
		String socialId = oauth2User.getName();
		GrantedAuthority[] grantedAuthorities = oauth2User.getAuthorities().toArray(new GrantedAuthority[0]);
		UserRole authority = UserRole.valueOf(grantedAuthorities[0].getAuthority());
		log.info("oauth2User 에서 꺼내온 Authority : {}", authority);
		OAuthAttribute authAttribute = OAuthAttribute.of(socialType, socialId, authority, oauth2User.getAttributes());
		User foundedUser = userValidationService.findByProviderAndProviderId(socialType, socialId)
			.map(user -> {
				//user가 전에 로그인 한 적 있음
				log.info("Already exists: {} for (social: {}, socialId: {})", user, social, socialId);
				user.setNickname(authAttribute.getNickname());
				return user;
			})
			.orElseGet(() -> {
				//처음 로그인 하면
				log.info("첫 로그인 감지. 자동 회원가입을 진행합니다.");
				return userRepository.save(authAttribute.toEntity());
			});
		if (foundedUser.isDeleted()) {
			throw new AccountExpiredException("삭제된 유저입니다.");
		}
		return new UserIdAndRoleDTO(foundedUser.getId(), foundedUser.getRole());
	}

	public void checkUser() {
		User foundedUser = userValidationService.findByProviderAndProviderId(socialType, socialId)
			.map(user -> {
				//user가 전에 로그인 한 적 있음
				log.info("Already exists: {} for (social: {}, socialId: {})", user, social, socialId);
				user.setNickname(authAttribute.getNickname());
				return user;
			})
			.orElseGet(() -> {
				//처음 로그인 하면
				log.info("첫 로그인 감지. 자동 회원가입을 진행합니다.");
				return userRepository.save(authAttribute.toEntity());
			});
		if (foundedUser.isDeleted()) {
			throw new AccountExpiredException("삭제된 유저입니다.");
		}
	}
}
