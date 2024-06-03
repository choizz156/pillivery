package com.team33.modulecore.payment.kakao.application;

import static com.team33.modulecore.payment.kakao.application.request.Params.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.kakao.application.refund.RefundContext;
import com.team33.modulecore.payment.kakao.application.request.Params;
import com.team33.modulecore.payment.kakao.dto.PaymentParams;

@Component
public class ParameterProvider {

	public Map<String, Object> getOneTimeReqsParams(Order order) {
		var commonReqsParams = getRequestParams(order);
		commonReqsParams.put(CID.name(), ONE_TIME_CID.getValue());
		commonReqsParams.put(APPROVAL_URL.name(), APPROVAL_URL.getValue() + "/" + order.getId());

		return commonReqsParams;
	}

	public Map<String, Object> getSubscriptionReqsParams(Order order) {
		var commonReqsParams = getRequestParams(order);
		commonReqsParams.put(CID.name(), CID.getValue());
		commonReqsParams.put(APPROVAL_URL.name(),
			Params.SUBSCRIPTION_APPROVAL_URI.getValue() + "/" + order.getId());
		return commonReqsParams;
	}

	public Map<String, Object> getOneTimeApproveParams(
		String tid,
		String pgToken,
		Long orderId
	) {
		var commonApproveParams =
			getCommonApproveParams(tid, pgToken, orderId);
		commonApproveParams.put(CID.name(), ONE_TIME_CID.getValue());
		return commonApproveParams;
	}

	public Map<String, Object> getSubscriptionFirstApproveParams(
		String tid,
		String pgToken,
		Long orderId
	) {
		var commonSubsParams = getCommonApproveParams(tid, pgToken, orderId);
		commonSubsParams.put(CID.name(), Params.SUBSCRIP_CID.getValue());

		return commonSubsParams;
	}

	public Map<String, Object> getSubscriptionApproveParams(Order order) {
		var subsApproveParams = getRequestParams(order);
		subsApproveParams.put(SID.name(), order.getSid());
		subsApproveParams.put(CID.name(), Params.SUBSCRIP_CID.getValue());

		return subsApproveParams;
	}

	public Map<String, Object> getRefundParams(RefundContext refundContext, String tid) {
		Map<String, Object> refundParam = new ConcurrentHashMap<>();

		refundParam.put(CANCEL_AMOUNT.name(), refundContext.getCancelAmount());
		refundParam.put(CANCEL_TAX_FREE_AMOUNT.name(), refundContext.getCancelTaxFreeAmount());
		refundParam.put(TID.name(), tid);
		refundParam.put(CID.name(), ONE_TIME_CID.getValue());

		return refundParam;
	}

	public Map<String, Object> getSubsCancelParams(Order order) {
		Map<String, Object> parameters = new ConcurrentHashMap<>();

		parameters.put(CID.name(), Params.SUBSCRIP_CID.getValue());
		parameters.put(SID.name(), order.getSid());

		return parameters;
	}

	public Map<String, Object> getLookupParams(String tid) {
		Map<String, Object> lookupParams = new ConcurrentHashMap<>();
		lookupParams.put(TID.name(), tid);
		lookupParams.put(CID.name(), ONE_TIME_CID.getValue());

		return lookupParams;
	}

	private Map<String, Object> getRequestParams(Order order) {
		PaymentParams requestParamsInfo = getRequestParamsInfo(order);
		return getCommonReqsParams(requestParamsInfo);
	}

	private Map<String, Object> getCommonApproveParams(
		String tid,
		String pgToken,
		Long orderId
	) {
		Map<String, Object> parameters = new ConcurrentHashMap<>();

		parameters.put(TID.name(), tid);
		parameters.put(PARTNER_ORDER_ID.name(), String.valueOf(orderId));
		parameters.put(PARTNER.name(), PARTNER.getValue());
		parameters.put(PG_TOKEN.name(), pgToken);

		return parameters;
	}

	private Map<String, Object> getCommonReqsParams(PaymentParams paymentParams) {
		Map<String, Object> parameters = new ConcurrentHashMap<>();

		parameters.put(PARTNER_ORDER_ID.name(), String.valueOf(paymentParams.getOrderId()));
		parameters.put(PARTNER_USER_ID.name(), PARTNER.getValue());
		parameters.put(ITEM_NAME.name(), paymentParams.getItemName());
		parameters.put(QUANTITY.name(), String.valueOf(paymentParams.getQuantity()));
		parameters.put(TOTAL_AMOUNT.name(), String.valueOf(paymentParams.getTotalAmount()));
		parameters.put(TAX_FREE_AMOUNT.name(), "0");
		parameters.put(CANCEL_URL.name(), CANCEL_URI.getValue());
		parameters.put(FAIL_URL.name(), FAIL_URI);

		return parameters;
	}

	private PaymentParams getRequestParamsInfo(Order order) {
		String itemName = getItemName(order);

		return PaymentParams.builder()
			.totalAmount(order.getTotalPrice())
			.quantity(order.getTotalQuantity())
			.itemName(itemName)
			.orderId(order.getId())
			.build();
	}

	private String getItemName(Order order) {
		int itemQuantity = order.getTotalItemsCount();
		String itemName = order.getMainItemName();

		if (itemQuantity == 1) {
			return itemName;
		}

		return itemName + " 그 외 " + (itemQuantity - 1) + "개";
	}
}
