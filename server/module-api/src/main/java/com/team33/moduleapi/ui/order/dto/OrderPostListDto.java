package com.team33.moduleapi.ui.order.dto;

import java.util.List;

import lombok.Data;

@Data
public class OrderPostListDto {
	private Long userId;
	private boolean subscription;
	private boolean orderedAtCart;
	private String city;
	private String detailAddress;
	private String realName;
	private String phoneNumber;
	private List<OrderPostDto> orderPostDtoList;
}
