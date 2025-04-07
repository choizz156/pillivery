package com.team33.moduleadmin.service;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import com.team33.moduleadmin.infra.SubscriptionOrderBatchDao;
import com.team33.modulecore.core.order.domain.entity.SubscriptionOrder;

@Service
public class SubscriptionOrderBatchService extends AbstractBatchService<SubscriptionOrder> {

    private final SubscriptionOrderBatchDao subscriptionOrderBatchDao;

    public SubscriptionOrderBatchService(DataSource jdbcDataSource, SubscriptionOrderBatchDao subscriptionOrderBatchDao) {
        super(jdbcDataSource);
        this.subscriptionOrderBatchDao = subscriptionOrderBatchDao;
    }

    @Override
    protected void executeBatchSave(List<SubscriptionOrder> entities) {
        subscriptionOrderBatchDao.saveAll(entities);
    }
}
