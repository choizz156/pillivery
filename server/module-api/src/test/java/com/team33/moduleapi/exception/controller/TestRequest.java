package com.team33.moduleapi.exception.controller;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestRequest {

	@NotBlank(message = "공백일 수 없습니다")
	private String name;

	@Min(value = 1, message = "1 이상이어야 합니다")
	private int age;

}
