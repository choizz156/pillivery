package com.team33.modulebatch.scheduler.job;

import org.springframework.boot.CommandLineRunner;

public interface JobRunner extends CommandLineRunner {

	@Override
	void run(String... args) throws Exception;
}
