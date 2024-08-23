package com.team33.moduleapi.api.order.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OrderPostListDto {
	@NotNull
	private Long userId;
	@NotNull
	private boolean subscription;
	@NotNull
	private boolean orderedAtCart;
	@NotBlank
	private String city;
	@NotBlank
	private String detailAddress;
	@NotBlank
	private String realName;
	@NotBlank
	private String phoneNumber;
	@NotNull
	private List<OrderPostDto> orderPostDtoList;
}
