package com.team33.moduleevent.domain.repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import com.team33.moduleevent.domain.EventStatus;
import com.team33.moduleevent.domain.EventType;
import com.team33.moduleevent.domain.entity.ApiEvent;

@EnableAutoConfiguration
@ContextConfiguration(classes = {
	EventRepository.class
})
@EnableJpaRepositories(basePackages = "com.team33.moduleevent.domain.repository")
@EntityScan(basePackages = "com.team33.moduleevent.domain.entity")
@DataJpaTest
class EventRepositoryTest {

	@Autowired
	private EventRepository eventRepository;

	@DisplayName("이벤트를 조회할 수 있다.")
	@Test
	void findTop20ByStatusOrderByCreatedAt() {
		//given
		IntStream.range(0, 10).forEach(
			i -> eventRepository.save(
				ApiEvent.builder()
					.type(EventType.SUBSCRIPTION_CANCELED)
					.createdAt(LocalDateTime.of(2020, 1, 1, i, 0, 0))
					.status(EventStatus.READY)
					.build()
			)
		);

		IntStream.range(1, 20).forEach(
			i -> eventRepository.save(
				ApiEvent.builder()
					.type(EventType.KAKAO_REFUNDED)
					.createdAt(LocalDateTime.of(2020, 1, 1, i, 0, 0))
					.status(EventStatus.COMPLETE)
					.build()
			)
		);

		//when
		List<ApiEvent> list = eventRepository.findTop20ByStatusOrderByCreatedAt(EventStatus.READY);

		//then
		Assertions.assertThat(list).hasSize(10)
			.isSortedAccordingTo(Comparator.comparing(ApiEvent::getCreatedAt));

	}
}