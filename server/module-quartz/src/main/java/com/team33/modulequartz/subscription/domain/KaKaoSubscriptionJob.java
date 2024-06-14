package com.team33.modulequartz.subscription.domain;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.stereotype.Component;

import com.team33.modulecore.exception.BusinessLogicException;
import com.team33.modulecore.exception.ExceptionCode;
import com.team33.modulecore.order.domain.OrderItem;
import com.team33.moduleexternalapi.infra.RestTemplateSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class KaKaoSubscriptionJob implements Job {

	private final RestTemplateSender restTemplateSender;

	@Override
	public void execute(JobExecutionContext context) {

		JobDataMap mergedJobDataMap = context.getMergedJobDataMap();

		OrderItem orderItem = (OrderItem) mergedJobDataMap.get("itemOrder");
		log.info("start itemOrderId = {}", orderItem.getId());
		log.info("itemOrder title = {}", orderItem.getItem().getProductName());

		long orderId = (long) mergedJobDataMap.get("orderId");
		log.info("start orderId = {}", orderId);

		if(orderId == 0) {
			throw new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND);
		}

		restTemplateSender.sendToPost("", "http://localhost:8080/payments/approve/subscriptions/"+orderId, null, String.class);
		// if (connectKaKaoPay(orderId) == null) {
		//     throw new JobExecutionException(ExceptionCode.PAYMENT_FAIL.getMessage());
		// }
	}

	// private String connectKaKaoPay(Long orderId) {
	//
	//     MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
	//
	//     parameters.add("orderId", String.valueOf(orderId));
	//
	//     URI uri = UriComponentsBuilder.newInstance()
	//         .scheme("http")
	//         .host("http://localhost:8080/")
	//         .port(8080)
	//         .path("/payments/kakao/subscription")
	//         .queryParams(parameters)
	//         .build().toUri();
	//
	//     return restTemplate.postForObject(uri, null, String.class);
	// }

}
