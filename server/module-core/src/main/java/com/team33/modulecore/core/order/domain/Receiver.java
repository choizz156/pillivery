package com.team33.modulecore.core.order.domain;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import com.team33.modulecore.core.user.domain.Address;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Embeddable
public class Receiver {

	private String realName;
	private String phone;
	@Embedded
	private Address address;

	@Builder
	public Receiver(String realName, String phone, Address address) {
		this.realName = realName;
		this.phone = phone;
		this.address = address;
	}

}
