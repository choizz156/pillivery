package com.team33.modulecore.domain.audit;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@MappedSuperclass // 해당 클래스를 상속받는 엔티티에서 해당클래스의 필드를 컬럼으로 사용가능
@EntityListeners(AuditingEntityListener.class) // Auditing기능을 수행하는 리스너를 등록
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
