package com.team33.moduleapi.api.user.dto;

import com.team33.modulecore.core.user.domain.entity.User;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserIdDto {
	private Long userId;
	private Long normalCartId;
	private Long subscriptionCartId;

	public UserIdDto(Long userId, Long normalCartId, Long subscriptionCartId) {
		this.userId = userId;
		this.normalCartId = normalCartId;
		this.subscriptionCartId = subscriptionCartId;
	}

	public static UserIdDto from(User user) {
		return new UserIdDto(user.getId(), user.getNormalCartId(), user.getSubscriptionCartId());
	}
}
