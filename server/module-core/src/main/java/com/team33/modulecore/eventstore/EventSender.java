package com.team33.modulecore.eventstore;

public interface EventSender {
	void send(ApiEventSet event);
}
