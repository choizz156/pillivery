package com.team33.modulebatch.step;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team33.modulebatch.domain.entity.DelayedItem;
import com.team33.modulebatch.infra.RestTemplateSender;

@ExtendWith(MockitoExtension.class)
class RetryItemProcessorTest {

	@Mock
	private RestTemplateSender restTemplateSender;

	@DisplayName("jobId가 설정되지 않으면 예외가 발생한다")
	@Test
	void test1() throws Exception {
		// given
		RetryItemProcessor processor = new RetryItemProcessor(restTemplateSender);
		DelayedItem delayedItem = DelayedItem.builder().build();

		// when then
		assertThatThrownBy(() -> processor.process(delayedItem))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("JobId가 설정돼있어야 합니다.");
	}

	@DisplayName("주문 정보로부터 idempotencyKey를 생성한다")
	@Test
	void test2() throws Exception {
		// given
		RetryItemProcessor processor = new RetryItemProcessor(restTemplateSender);
		processor.initialize(1L);
		LocalDate originalPaymentDate = LocalDate.of(2002, 1, 1);
		DelayedItem delayedItem = DelayedItem.builder()
			.subscriptionOrderId(1L)
			.originalPaymentDate(originalPaymentDate)
			.retryCount(1L)
			.build();

		// when
		DelayedItem result = processor.process(delayedItem);

		// then
		String expectedKey = "1_1_" + originalPaymentDate + "_1_RT";
		assertThat(result.getIdempotencyKey()).isNotBlank();
		assertThat(result.getIdempotencyKey()).isEqualTo(expectedKey);
	}

	@DisplayName("멱등키에 이상이 없을 시 API 통신을 한다.")
	@Test
	void test3() throws Exception {
		//given
		RetryItemProcessor processor = new RetryItemProcessor(restTemplateSender);
		processor.initialize(1L);
		LocalDate originalPaymentDate = LocalDate.of(2002, 1, 1);
		DelayedItem delayedItem = DelayedItem.builder()
			.subscriptionOrderId(1L)
			.originalPaymentDate(originalPaymentDate)
			.retryCount(1L)
			.build();

		//when
		processor.process(delayedItem);

		//then
		Mockito.verify(restTemplateSender, Mockito.times(1)).sendToPost(anyLong(),anyString(),eq(null));
	}

	@DisplayName("멱등 키가 중복일 경우 api 통신을 하지 않는다.")
	@Test
	void test4() throws Exception {
		//given
		RetryItemProcessor processor = new RetryItemProcessor(restTemplateSender);
		processor.initialize(1L);

		LocalDate originalPaymentDate = LocalDate.of(2002, 1, 1);
		DelayedItem delayedItem1 = DelayedItem.builder()
			.subscriptionOrderId(1L)
			.originalPaymentDate(originalPaymentDate)
			.retryCount(1L)
			.build();

		DelayedItem delayedItem2 = DelayedItem.builder()
			.subscriptionOrderId(1L)
			.originalPaymentDate(originalPaymentDate)
			.retryCount(1L)
			.build();

		processor.process(delayedItem1);

		//when then
		assertThatThrownBy(() -> processor.process(delayedItem2))
			.isInstanceOf(IllegalArgumentException.class);

	}
}