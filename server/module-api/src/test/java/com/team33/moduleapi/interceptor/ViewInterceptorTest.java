package com.team33.moduleapi.interceptor;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import com.team33.moduleapi.ApiTest;

class ViewInterceptorTest extends ApiTest {

	@Autowired
	private RedissonClient redissonClient;

	@DisplayName("같은 ip가 중복조회 할 경우 조회수가 증가하지 않는다.")
	@Test
	void 중복_조회수_증가() throws Exception {
		//given
		ViewInterceptor viewInterceptor = new ViewInterceptor(redissonClient);

		MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.setRemoteAddr("127.0.0.1");
		mockHttpServletRequest.setRequestURI("/test/1");

		//when
		for (int i = 0; i < 100; i++) {
			viewInterceptor.postHandle(mockHttpServletRequest, null, null, null);
		}

		//then
		assertThat(redissonClient.getHyperLogLog("1").count()).isEqualTo(1L);
	}

	@DisplayName("다른 ip가 조회할 경우 조회수가 증가한다.")
	@Test
	void 조회수_증가() throws Exception {
		//given
		ViewInterceptor viewInterceptor = new ViewInterceptor(redissonClient);

		MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

		mockHttpServletRequest.setRequestURI("/test/1");

		//when
		for (int i = 0; i < 100; i++) {
			mockHttpServletRequest.setRemoteAddr("127.0.0.1" + i);
			viewInterceptor.postHandle(mockHttpServletRequest, null, null, null);
		}

		//then
		assertThat(redissonClient.getHyperLogLog("1").count()).isEqualTo(100L);
	}

}