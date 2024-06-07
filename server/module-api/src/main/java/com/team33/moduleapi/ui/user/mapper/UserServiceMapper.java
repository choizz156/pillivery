package com.team33.moduleapi.ui.user.mapper;

import org.springframework.stereotype.Component;

import com.team33.moduleapi.ui.user.dto.UserPatchDto;
import com.team33.moduleapi.ui.user.dto.UserPostDto;
import com.team33.moduleapi.ui.user.dto.UserPostOauthDto;
import com.team33.modulecore.user.domain.Address;
import com.team33.modulecore.user.dto.OAuthUserServiceDto;
import com.team33.modulecore.user.dto.UserServicePatchDto;
import com.team33.modulecore.user.dto.UserServicePostDto;

@Component
public class UserServiceMapper {
	public UserServicePostDto toUserPost(UserPostDto userDto) {
		Address address = new Address(userDto.getCity(), userDto.getDetailAddress());
		return UserServicePostDto.builder().email(userDto.getEmail())
			.displayName(userDto.getDisplayName())
			.address(address)
			.password(userDto.getPassword())
			.realName(userDto.getRealName())
			.phone(userDto.getPhone())
			.build();
	}

	public OAuthUserServiceDto toOauthPost(UserPostOauthDto userDto) {
		return OAuthUserServiceDto.builder()
			.email(userDto.getEmail())
			.address(new Address(userDto.getCity(), userDto.getDetailAddress()))
			.displayName(userDto.getDisplayName())
			.phone(userDto.getPhone())
			.build();
	}

	public UserServicePatchDto toUserPatch(UserPatchDto userDto) {

		return UserServicePatchDto.builder()
			.password(userDto.getPassword())
			.address(new Address(userDto.getCity(), userDto.getDetailAddress()))
			.displayName(userDto.getDisplayName())
			.realName(userDto.getRealName())
			.phone(userDto.getPhone())
			.build();
	}
}
