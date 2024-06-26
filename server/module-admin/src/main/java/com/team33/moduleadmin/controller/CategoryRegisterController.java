package com.team33.moduleadmin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleadmin.dto.CategoryPostDto;
import com.team33.moduleadmin.service.CategoryRegisterService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/categories", method = RequestMethod.POST)
public class CategoryRegisterController {

	private final CategoryRegisterService categoryRegisterService;

	@ResponseStatus(HttpStatus.CREATED)
	public void registerCategory(@RequestBody CategoryPostDto postDto) {
		categoryRegisterService.save(postDto.getCategoryName());
	}
}
