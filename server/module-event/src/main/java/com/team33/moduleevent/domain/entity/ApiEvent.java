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

import org.hibernate.annotations.DynamicUpdate;

import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.EventType;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "api_event")
@Entity
public class ApiEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "api_event_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	private EventType type;

	private String contentType;

	private String parameters;

	private String url;

	@Enumerated(EnumType.STRING)
	private EventStatus status;

	private LocalDateTime createdAt;

	@Builder
	public ApiEvent(
		EventType type,
		String contentType,
		String parameters,
		String url,
		EventStatus status,
		LocalDateTime createdAt
	) {
		this.type = type;
		this.contentType = contentType;
		this.parameters = parameters;
		this.url = url;
		this.status = status;
		this.createdAt = createdAt;
	}

	public void changeStatusToComplete() {
		this.status = EventStatus.COMPLETE;
	}

	public void changeStatusToFail() {
		this.status = EventStatus.FAILED;
	}
}
