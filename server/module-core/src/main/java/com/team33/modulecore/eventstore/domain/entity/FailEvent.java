package com.team33.modulecore.eventstore.domain.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.team33.modulecore.eventstore.domain.EventStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class FailEvent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fail_event_id")
	private Long id;

	private String type;

	private String payload;

	private String reason;

	@Enumerated(EnumType.STRING)
	private EventStatus status;

	private LocalDateTime createdAt;

	@Builder
	public FailEvent(String type, String payload, String reason) {
		this.type = type;
		this.payload = payload;
		this.reason = reason;
		this.status = EventStatus.FAILED;
		this.createdAt = LocalDateTime.now();
	}
}
