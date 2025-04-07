package com.team33.moduleadmin.service;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import com.team33.moduleadmin.infra.OrderItemBatchDao;
import com.team33.moduleadmin.infra.UserBatchDao;
import com.team33.modulecore.core.order.domain.entity.OrderItem;
import com.team33.modulecore.core.user.domain.entity.User;

@Service
public class OrderItemBatchService extends AbstractBatchService<OrderItem> {

    private final OrderItemBatchDao orderItemBatchDao;

    public OrderItemBatchService(DataSource jdbcDataSource, OrderItemBatchDao orderItemBatchDao) {
        super(jdbcDataSource);
        this.orderItemBatchDao = orderItemBatchDao;
    }

    @Override
    protected void executeBatchSave(List<OrderItem> entities) {
        orderItemBatchDao.saveAll(entities);
    }
}
