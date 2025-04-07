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

import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public abstract class AbstractBatchService<T> {

    private final DataSource jdbcDataSource;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

    public void saveAll(List<T> entities) {
        HikariDataSource hikariDataSource = (HikariDataSource) jdbcDataSource;
        ExecutorService executorService = Executors.newFixedThreadPool(hikariDataSource.getMaximumPoolSize());
        List<List<T>> entitySubList = getEntitySubList(entities);

        List<Callable<Void>> collect = entitySubList.stream().map(subList -> (Callable<Void>)() -> {
            executeBatchSave(subList);
            return null;
        }).collect(Collectors.toList());

        try {
            executorService.invokeAll(collect);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    private List<List<T>> getEntitySubList(List<T> entities) {
        List<List<T>> listOfSubList = new ArrayList<>(batchSize + 4);
        for (int i = 0; i < entities.size(); i += batchSize) {
            if (i + batchSize <= entities.size()) {
                listOfSubList.add(entities.subList(i, i + batchSize));
            } else {
                listOfSubList.add(entities.subList(i, entities.size()));
            }
        }
        return listOfSubList;
    }

    protected abstract void executeBatchSave(List<T> entities);
}