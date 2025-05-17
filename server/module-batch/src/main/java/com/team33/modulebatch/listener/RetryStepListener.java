package com.team33.modulebatch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;

import com.team33.modulebatch.domain.entity.DelayedItem;

public class RetryStepListener implements ItemReadListener<DelayedItem> {

	private static final Logger LOGGER = LoggerFactory.getLogger("fileLog");
	@Override
	public void beforeRead() {

	}

	@Override
	public void afterRead(DelayedItem item) {

		if (item == null) {
			LOGGER.info("retry item is null");
		}
	}

	@Override
	public void onReadError(Exception ex) {
		LOGGER.warn("Retry item Reader Exception", ex);
	}
}
