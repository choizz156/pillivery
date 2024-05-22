package com.team33.moduleexternalapi.domain;

import com.team33.moduleexternalapi.infra.RefundParams;

public interface RefundClient<T> {

	T send(RefundParams params, String url);
}
