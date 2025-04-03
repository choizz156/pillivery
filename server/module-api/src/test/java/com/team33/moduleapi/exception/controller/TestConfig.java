package com.team33.moduleapi.exception.controller;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

	@Bean
	public TestExceptionController testExceptionController() {
		return new TestExceptionController();
	}
}
