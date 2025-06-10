package com.team33.modulecore.cache;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.github.benmanes.caffeine.cache.Expiry;

public class CategoryItemsExpiry implements Expiry<Object, Object> {

	private static final int BASE_TTL = 7;
	private static final int JITTER_RANGE = 4;

	@Override
	public long expireAfterCreate(@NonNull Object key, @NonNull Object value, long currentTime) {

		int random = ThreadLocalRandom.current().nextInt(0, JITTER_RANGE + 1);

		long jitter = TimeUnit.MINUTES.toNanos(random);
		long baseDays = TimeUnit.DAYS.toNanos(BASE_TTL);

		return baseDays + jitter;
	}

	@Override
	public long expireAfterUpdate(@NonNull Object key, @NonNull Object value, long currentTime,
		@NonNegative long currentDuration) {

		return 0;
	}

	@Override
	public long expireAfterRead(@NonNull Object key, @NonNull Object value, long currentTime,
		@NonNegative long currentDuration) {

		return 0;
	}
}
