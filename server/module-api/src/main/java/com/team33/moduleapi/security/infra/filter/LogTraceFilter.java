package com.team33.moduleapi.security.infra.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;

public class LogTraceFilter extends OncePerRequestFilter {

	private static final String TRACE_ID = "TRACE_ID";

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {

		String requestId = request.getHeader("X-RequestID");
		MDC.put(TRACE_ID, StringUtils.defaultString(requestId, UUID.randomUUID().toString()));
		filterChain.doFilter(request, response);
		MDC.clear();
	}
}
