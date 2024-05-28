package com.team33.modulecore.eventstore.api;

import com.team33.modulecore.eventstore.domain.ApiEventSet;

public interface EventSender {
	void send(ApiEventSet event);
}
