package com.team33.modulecore.core.payment.kakao.application;

import static com.team33.modulecore.core.payment.kakao.application.request.Params.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.team33.modulecore.core.order.domain.entity.Order;
import com.team33.modulecore.core.payment.kakao.application.refund.RefundContext;
import com.team33.modulecore.core.payment.kakao.application.request.Params;
import com.team33.modulecore.core.payment.kakao.dto.PaymentParams;

@Component
public class ParameterProvider {

	public Map<String, Object> getOneTimeReqsParams(Order order) {
		var commonReqsParams = getRequestParams(order);
		commonReqsParams.put(Params.CID.getValue(), ONE_TIME_CID.getValue());
		commonReqsParams.put(Params.APPROVAL_URL.getValue(), Params.ONE_TIME_APPROVAL_URL.getValue() + "/" + order.getId());

		return commonReqsParams;
	}

	public Map<String, Object> getSubscriptionReqsParams(Order order) {
		var commonReqsParams = getRequestParams(order);
		commonReqsParams.put(Params.CID.getValue(), SUBSCRIPTION_CID.getValue());
		commonReqsParams.put(Params.APPROVAL_URL.getValue(), Params.SUBSCRIPTION_APPROVAL_URL.getValue() + "/" + order.getId());
		return commonReqsParams;
	}

	public Map<String, Object> getOneTimeApproveParams(
		String tid,
		String pgToken,
		Long orderId
	) {
		var commonApproveParams =
			getCommonApproveParams(tid, pgToken, orderId);
		commonApproveParams.put(Params.CID.getValue(), ONE_TIME_CID.getValue());
		return commonApproveParams;
	}

	public Map<String, Object> getSubscriptionFirstApproveParams(
		String tid,
		String pgToken,
		Long orderId
	) {
		var commonSubsParams = getCommonApproveParams(tid, pgToken, orderId);
		commonSubsParams.put(Params.CID.getValue(), SUBSCRIPTION_CID.getValue());

		return commonSubsParams;
	}

	public Map<String, Object> getSubscriptionApproveParams(Order order) {
		var subsApproveParams = getRequestParams(order);
		subsApproveParams.put(Params.SID.getValue(), order.getSid());
		subsApproveParams.put(Params.CID.getValue(), SUBSCRIPTION_CID.getValue());

		return subsApproveParams;
	}

	public Map<String, Object> getRefundParams(RefundContext refundContext, String tid) {
		Map<String, Object> refundParam = new ConcurrentHashMap<>();

		refundParam.put(Params.CANCEL_AMOUNT.getValue(), refundContext.getCancelAmount());
		refundParam.put(Params.CANCEL_TAX_FREE_AMOUNT.getValue(), refundContext.getCancelTaxFreeAmount());
		refundParam.put(Params.TID.getValue(), tid);
		refundParam.put(Params.CID.getValue(), ONE_TIME_CID.getValue());

		return refundParam;
	}

	public Map<String, Object> getSubsCancelParams(String sid) {
		Map<String, Object> parameters = new ConcurrentHashMap<>();

		parameters.put(Params.CID.getValue(), SUBSCRIPTION_CID.getValue());
		parameters.put(Params.SID.getValue(), sid);

		return parameters;
	}

	public Map<String, Object> getLookupParams(String tid) {
		Map<String, Object> lookupParams = new ConcurrentHashMap<>();
		lookupParams.put(Params.TID.getValue(), tid);
		lookupParams.put(Params.CID.getValue(), ONE_TIME_CID.getValue());

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

		parameters.put(Params.TID.getValue(), tid);
		parameters.put(Params.PARTNER_ORDER_ID.getValue(), String.valueOf(orderId));
		parameters.put(Params.PARTNER_USER_ID.getValue(), Params.PARTNER.getValue());
		parameters.put(Params.PG_TOKEN.getValue(), pgToken);

		return parameters;
	}

	private Map<String, Object> getCommonReqsParams(PaymentParams paymentParams) {
		Map<String, Object> parameters = new ConcurrentHashMap<>();

		parameters.put(Params.PARTNER_ORDER_ID.getValue(), String.valueOf(paymentParams.getOrderId()));
		parameters.put(Params.PARTNER_USER_ID.getValue(), Params.PARTNER.getValue());
		parameters.put(Params.ITEM_NAME.getValue(), paymentParams.getItemName());
		parameters.put(Params.QUANTITY.getValue(), String.valueOf(paymentParams.getQuantity()));
		parameters.put(Params.TOTAL_AMOUNT.getValue(), String.valueOf(paymentParams.getTotalAmount()));
		parameters.put(Params.TAX_FREE_AMOUNT.getValue(), "0");
		parameters.put(Params.CANCEL_URL.getValue(), CANCEL_URL.getValue());
		parameters.put(Params.FAIL_URL.getValue(), FAIL_URL.getValue());

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
