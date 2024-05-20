package com.team33.moduleexternalapi.domain;

import java.util.Map;


public interface PaymentClient<T> {

	T send(Map<String, String> params, String url);
}
