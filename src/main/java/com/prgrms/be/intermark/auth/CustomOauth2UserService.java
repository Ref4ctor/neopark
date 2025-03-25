package com.prgrms.be.intermark.auth;

import java.util.Optional;

import javax.security.auth.login.AccountExpiredException;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional(readOnly = true)
public class CustomOauth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	//로그인하면 이쪽으로 접근
	//성공하면 성공핸들러, 실패하면 실패핸들러
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		//google인데 대문자로 변환해서 GOOGLE
		SocialType socialType = SocialType.valueOf(userRequest.getClientRegistration()
			.getRegistrationId()
			.toUpperCase());
		String socialId = oAuth2User.getName();

		//회원인지 체크
		Optional<User> userAlreadyExist = userRepository.findBySocialTypeAndSocialIdAndIsDeletedFalse(socialType,
			socialId);
		if (userAlreadyExist.isPresent()) {
			UserRole role = userAlreadyExist.get().getRole();
			return new CustomUserPrincipal("sub", role, oAuth2User.getAttributes());
		}
		//OAuth2user를  return 해줘야 한다.
		//회원이 아니면 ROLE_USER로 생성
		return new CustomUserPrincipal("sub", UserRole.ROLE_USER, oAuth2User.getAttributes());
	}

	@Transactional
	public UserIdAndRoleDTO join(OAuth2User oauth2User, String social) throws AccountExpiredException {
		SocialType socialType = SocialType.valueOf(social.toUpperCase());
		String socialId = oauth2User.getName();
		GrantedAuthority[] grantedAuthorities = oauth2User.getAuthorities().toArray(new GrantedAuthority[0]);
		UserRole authority = UserRole.valueOf(grantedAuthorities[0].getAuthority());
		log.info("oauth2User 에서 꺼내온 Authority : {}", authority);
		OAuthAttribute authAttribute = OAuthAttribute.of(socialType, socialId, authority, oauth2User.getAttributes());
		User foundedUser = findByProviderAndProviderId(socialType, socialId)
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
			log.info("탈퇴했던 회원이 회원가입을 다시 진행했습니다.");
			foundedUser.activateUser();
		}
		return new UserIdAndRoleDTO(foundedUser.getId(), foundedUser.getRole());
	}

	public Optional<User> findByProviderAndProviderId(SocialType social, String socialId) {
		return userRepository.findBySocialTypeAndSocialId(social, socialId);
	}

}
