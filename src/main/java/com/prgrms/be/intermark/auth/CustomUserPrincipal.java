package com.prgrms.be.intermark.auth;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.prgrms.be.intermark.domain.newerd.user.model.UserRole;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomUserPrincipal implements OAuth2User {

	private final String nameAttributeKey;
	private final UserRole authority;
	private final Map<String, Object> attributes;

	@Override
	public Map<String, Object> getAttributes() {

		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(authority.getKey()));
	}

	@Override
	public String getName() {
		return this.getAttributes().get(nameAttributeKey).toString();
	}

}
