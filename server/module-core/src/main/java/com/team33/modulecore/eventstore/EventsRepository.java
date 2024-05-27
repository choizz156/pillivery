package com.team33.modulecore.eventstore;

import java.util.List;

public interface EventsRepository{

	void save(Object event);

	List<ApiEventSet> get(long offset, long limit);
}
