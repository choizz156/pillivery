package com.team33.moduleadmin.service;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import com.team33.moduleadmin.infra.OrderBatchDao;
import com.team33.modulecore.core.order.domain.entity.Order;

@Service
public class OrderBatchService extends AbstractBatchService<Order> {

    private final OrderBatchDao orderBatchDao;

    public OrderBatchService(DataSource jdbcDataSource, OrderBatchDao orderBatchDao) {
        super(jdbcDataSource);
        this.orderBatchDao = orderBatchDao;
    }

    @Override
    protected void executeBatchSave(List<Order> entities) {
        orderBatchDao.saveAll(entities);
    }
}
