package com.team33.modulecore.user.domain;

import javax.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Embeddable
public class ReviewId {

	private Long id;

	public ReviewId(Long id) {
		this.id = id;
	}
}
