package com.team33.modulecore.eventstore.domain.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;

import com.team33.modulecore.eventstore.domain.EventStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DynamicUpdate
public class ApiEventSet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_id")
	private Long id;

	private String type;

	private String contentType;

	private String parameters;

	private String url;

	@Enumerated(EnumType.STRING)
	private EventStatus status;

	@CreatedDate
	private LocalDateTime createdAt;

	@Builder
	public ApiEventSet(String type, String contentType, String parameters, String url) {
		this.type = type;
		this.contentType = contentType;
		this.parameters = parameters;
		this.url = url;
		this.status = EventStatus.READY;
		this.createdAt = LocalDateTime.now();
	}

	public void changeStatusToComplete() {
		this.status = EventStatus.COMPLETE;
	}

	public void changeStatusToFail() {
		this.status = EventStatus.FAILED;
	}
}
