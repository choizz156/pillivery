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
		commonReqsParams.put(CID.getValue(), ONE_TIME_CID.getValue());
		commonReqsParams.put(APPROVAL_URL.getValue(), ONE_TIME_APPROVAL_URL.getValue() + "/" + order.getId());

		return commonReqsParams;
	}

	public Map<String, Object> getSubscriptionReqsParams(Order order) {
		var commonReqsParams = getRequestParams(order);
		commonReqsParams.put(CID.getValue(), CID.getValue());
		commonReqsParams.put(APPROVAL_URL.getValue(), SUBSCRIPTION_APPROVAL_URI.getValue() + "/" + order.getId());
		return commonReqsParams;
	}

	public Map<String, Object> getOneTimeApproveParams(
		String tid,
		String pgToken,
		Long orderId
	) {
		var commonApproveParams =
			getCommonApproveParams(tid, pgToken, orderId);
		commonApproveParams.put(CID.getValue(), ONE_TIME_CID.getValue());
		return commonApproveParams;
	}

	public Map<String, Object> getSubscriptionFirstApproveParams(
		String tid,
		String pgToken,
		Long orderId
	) {
		var commonSubsParams = getCommonApproveParams(tid, pgToken, orderId);
		commonSubsParams.put(CID.getValue(), Params.SUBSCRIP_CID.getValue());

		return commonSubsParams;
	}

	public Map<String, Object> getSubscriptionApproveParams(Order order) {
		var subsApproveParams = getRequestParams(order);
		subsApproveParams.put(SID.getValue(), order.getSid());
		subsApproveParams.put(CID.getValue(), Params.SUBSCRIP_CID.getValue());

		return subsApproveParams;
	}

	public Map<String, Object> getRefundParams(RefundContext refundContext, String tid) {
		Map<String, Object> refundParam = new ConcurrentHashMap<>();

		refundParam.put(CANCEL_AMOUNT.getValue(), refundContext.getCancelAmount());
		refundParam.put(CANCEL_TAX_FREE_AMOUNT.getValue(), refundContext.getCancelTaxFreeAmount());
		refundParam.put(TID.getValue(), tid);
		refundParam.put(CID.getValue(), ONE_TIME_CID.getValue());

		return refundParam;
	}

	public Map<String, Object> getSubsCancelParams(Order order) {
		Map<String, Object> parameters = new ConcurrentHashMap<>();

		parameters.put(CID.getValue(), Params.SUBSCRIP_CID.getValue());
		parameters.put(SID.getValue(), order.getSid());

		return parameters;
	}

	public Map<String, Object> getLookupParams(String tid) {
		Map<String, Object> lookupParams = new ConcurrentHashMap<>();
		lookupParams.put(TID.getValue(), tid);
		lookupParams.put(CID.getValue(), ONE_TIME_CID.getValue());

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

		parameters.put(TID.getValue(), tid);
		parameters.put(PARTNER_ORDER_ID.getValue(), String.valueOf(orderId));
		parameters.put(PARTNER.getValue(), PARTNER.getValue());
		parameters.put(PG_TOKEN.getValue(), pgToken);

		return parameters;
	}

	private Map<String, Object> getCommonReqsParams(PaymentParams paymentParams) {
		Map<String, Object> parameters = new ConcurrentHashMap<>();

		parameters.put(PARTNER_ORDER_ID.getValue(), String.valueOf(paymentParams.getOrderId()));
		parameters.put(PARTNER_USER_ID.getValue(), PARTNER.getValue());
		parameters.put(ITEM_NAME.getValue(), paymentParams.getItemName());
		parameters.put(QUANTITY.getValue(), String.valueOf(paymentParams.getQuantity()));
		parameters.put(TOTAL_AMOUNT.getValue(), String.valueOf(paymentParams.getTotalAmount()));
		parameters.put(TAX_FREE_AMOUNT.getValue(), "0");
		parameters.put(CANCEL_URL.getValue(), CANCEL_URI.getValue());
		parameters.put(FAIL_URL.getValue(), FAIL_URI.getValue());

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
