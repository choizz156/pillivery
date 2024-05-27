package com.team33.modulecore.eventstore;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.data.annotation.CreatedDate;

import lombok.Getter;

@Getter
@Entity
public class OffsetEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "offset_id")
	private Long id;

	private long nextOffset = 0L;

	@CreatedDate
	private LocalDateTime createdAt;
}
