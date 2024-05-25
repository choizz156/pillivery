package com.team33.modulecore.exception;

import org.springframework.dao.DataAccessException;

public class DataSaveException extends DataAccessException {

	public DataSaveException(String msg) {
		super(msg);
	}

	public DataSaveException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
