package com.team33.moduleadmin.repository;

import java.util.List;

import com.team33.modulecore.user.domain.entity.User;

public interface UserBatchRepository {
	void saveAll(List<User> users);
}
