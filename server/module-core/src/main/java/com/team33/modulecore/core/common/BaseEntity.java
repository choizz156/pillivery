package com.team33.modulecore.core.common;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    private static final String ASIA_SEOUL = "Asia/Seoul";
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @PrePersist
    public void prePersist(){
        this.createdAt = ZonedDateTime.now(ZoneId.of(ASIA_SEOUL));
        this.updatedAt = ZonedDateTime.now(ZoneId.of(ASIA_SEOUL));
    }
    @PreUpdate
    public void preUpdate(){
        this.updatedAt = ZonedDateTime.now(ZoneId.of(ASIA_SEOUL));
    }

}
