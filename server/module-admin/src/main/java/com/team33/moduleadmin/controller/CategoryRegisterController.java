package com.team33.moduleadmin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.team33.moduleadmin.dto.CategoryPostDto;
import com.team33.moduleadmin.service.CategoryRegisterService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/categories")
public class CategoryRegisterController {

	private static final Logger log = LoggerFactory.getLogger(CategoryRegisterController.class);
	private final CategoryRegisterService categoryRegisterService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void registerCategory(@RequestBody CategoryPostDto postDto) {
		categoryRegisterService.save(postDto.getCategoryName());
	}
}
