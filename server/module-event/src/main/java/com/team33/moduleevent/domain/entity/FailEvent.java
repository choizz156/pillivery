package com.team33.moduleevent.domain.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.EventType;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fail_events")
@Entity
public class FailEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fail_event_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	private EventType type;

	private String payload;

	private String reason;

	@Enumerated(EnumType.STRING)
	private EventStatus status;

	@CreatedDate
	private LocalDateTime createdAt;

	@Builder
	public FailEvent(EventType type, String payload, String reason, LocalDateTime createdAt) {
		this.type = type;
		this.payload = payload;
		this.reason = reason;
		this.status = EventStatus.FAILED;
		this.createdAt = createdAt;
	}
}
