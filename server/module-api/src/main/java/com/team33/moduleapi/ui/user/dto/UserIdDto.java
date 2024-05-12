package com.team33.moduleapi.ui.user.dto;

import com.team33.modulecore.user.domain.User;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserIdDto {
	private Long userId;
	private Long cartId;

	public UserIdDto(Long userId, Long cartId) {
		this.userId = userId;
		this.cartId = cartId;
	}

	public static UserIdDto from(User user) {
		return new UserIdDto(user.getId(), user.getCartId());
	}
}
