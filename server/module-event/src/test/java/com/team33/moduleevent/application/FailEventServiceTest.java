package com.team33.moduleevent.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import com.team33.moduleevent.domain.entity.ApiEvent;
import com.team33.moduleevent.domain.entity.FailEvent;
import com.team33.moduleevent.domain.repository.FailEventRepository;

@ExtendWith(OutputCaptureExtension.class)
class FailEventServiceTest {

	@DisplayName("실패한 이벤트를 저장할 수 있다.")
	@Test
	void 실패_이벤트_저장(CapturedOutput output) throws Exception{
		//given
		FailEventRepository failEventRepository = mock(FailEventRepository.class);
		ApiEvent apiEvent = mock(ApiEvent.class);
		FailEventService failEventService = new FailEventService(failEventRepository);

		//when
		failEventService.saveFailEvent(apiEvent,"reason");

		//then
		verify(failEventRepository, times(1)).save(any(FailEvent.class));
		assertThat(output).contains("eventId : 0, type : null, reason : reason");
	}

}