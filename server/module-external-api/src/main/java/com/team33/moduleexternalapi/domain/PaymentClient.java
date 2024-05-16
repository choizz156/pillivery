package com.team33.moduleexternalapi.domain;

import org.springframework.util.MultiValueMap;


public interface PaymentClient<T> {

	T send(MultiValueMap<String, String> params, String url);
}
