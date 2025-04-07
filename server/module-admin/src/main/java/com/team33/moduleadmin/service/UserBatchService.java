package com.team33.moduleadmin.service;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import com.team33.moduleadmin.infra.UserBatchDao;
import com.team33.modulecore.core.user.domain.entity.User;

@Service
public class UserBatchService extends AbstractBatchService<User> {

    private final UserBatchDao userBatchDao;

    public UserBatchService(DataSource jdbcDataSource, UserBatchDao userBatchDao) {
        super(jdbcDataSource);
        this.userBatchDao = userBatchDao;
    }

    @Override
    protected void executeBatchSave(List<User> entities) {
        userBatchDao.saveAll(entities);
    }
}
