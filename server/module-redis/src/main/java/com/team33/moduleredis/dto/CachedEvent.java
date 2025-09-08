package com.team33.moduleredis.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
@Getter
public class CachedEvent {

	private final String cacheName;
	private final String cacheKey;
}

