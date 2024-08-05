package com.team33.moduleadmin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.team33.moduleadmin.repository.UserBatchRepository;
import com.team33.modulecore.core.user.domain.entity.User;
import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserBatchService {

	private final DataSource jdbcDataSource;
	private final UserBatchRepository userBatchRepositoryImpl;

	@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
	private int batchSize;

	public void saveAll(List<User> users) {
		HikariDataSource hikariDataSource = (HikariDataSource) jdbcDataSource;
		ExecutorService executorService = Executors.newFixedThreadPool( hikariDataSource.getMaximumPoolSize());
		List<List<User>> userSubList = getUserSubList(users);

		List<Callable<Void>> collect = userSubList.stream().map(subList -> (Callable<Void>)() -> {
			userBatchRepositoryImpl.saveAll(subList);
			return null;
		}).collect(Collectors.toList());

		try {
			executorService.invokeAll(collect);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private List<List<User>> getUserSubList(List<User> users) {
		List<List<User>> listOfSubList = new ArrayList<>(batchSize + 4);
		for (int i = 0; i < users.size(); i += batchSize) {
			if (i + batchSize <= users.size()) {
				listOfSubList.add(users.subList(i, i + batchSize));
			} else {
				listOfSubList.add(users.subList(i, users.size()));
			}
		}
		return listOfSubList;
	}
}
