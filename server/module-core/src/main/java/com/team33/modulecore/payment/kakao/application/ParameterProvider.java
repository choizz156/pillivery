package com.team33.modulecore.payment.kakao.application;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.kakao.application.refund.RefundContext;
import com.team33.modulecore.payment.kakao.dto.PaymentParams;

@Component
public class ParameterProvider extends ParamsConst {

	public Map<String, Object> getOneTimeReqsParams(Order order) {
		var commonReqsParams = getRequestParams(order);
		commonReqsParams.put(CID, ONE_TIME_CID);
		commonReqsParams.put(APPROVAL_URL, ONE_TIME_APPROVAL_URL + "/" + order.getId());

		return commonReqsParams;
	}

	public Map<String, Object> getSubscriptionReqsParams(Order order) {
		var commonReqsParams = getRequestParams(order);
		commonReqsParams.put(CID, SUBSCRIP_CID);
		commonReqsParams.put(APPROVAL_URL, SUBSCRIPTION_APPROVAL_URI + "/" + order.getId());
		return commonReqsParams;
	}

	public Map<String, Object> getOneTimeApproveParams(
		String tid,
		String pgToken,
		Long orderId
	) {
		var commonApproveParams =
			getCommonApproveParams(tid, pgToken, orderId);
		commonApproveParams.put(CID, ONE_TIME_CID);
		return commonApproveParams;
	}

	public Map<String, Object> getSubscriptionFirstApproveParams(
		String tid,
		String pgToken,
		Long orderId
	) {
		var commonSubsParams = getCommonApproveParams(tid, pgToken, orderId);
		commonSubsParams.put(CID, SUBSCRIP_CID);

		return commonSubsParams;
	}

	public Map<String, Object> getSubscriptionApproveParams(Order order) {
		var subsApproveParams = getRequestParams(order);
		subsApproveParams.put(SID, order.getSid());
		subsApproveParams.put(CID, SUBSCRIP_CID);

		return subsApproveParams;
	}

	public Map<String, Object> getRefundParams(RefundContext refundContext, String tid) {
		Map<String, Object> refundParam = new ConcurrentHashMap<>();

		refundParam.put(CANCEL_AMOUNT, refundContext.getCancelAmount());
		refundParam.put(CANCEL_TAX_FREE_AMOUNT, refundContext.getCancelTaxFreeAmount());
		refundParam.put(TID, tid);
		refundParam.put(CID, ONE_TIME_CID);

		return refundParam;
	}

	public Map<String, Object> getSubsCancelParams(Order order) {
		Map<String, Object> parameters = new ConcurrentHashMap<>();

		parameters.put(CID, SUBSCRIP_CID);
		parameters.put(SID, order.getSid());

		return parameters;
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

		parameters.put(TID, tid);
		parameters.put(PARTNER_ORDER_ID, String.valueOf(orderId));
		parameters.put(PARTNER_USER_ID, PARTNER);
		parameters.put(PG_TOKEN, pgToken);

		return parameters;
	}

	private Map<String, Object> getCommonReqsParams(PaymentParams paymentParams) {
		Map<String, Object> parameters = new ConcurrentHashMap<>();

		parameters.put(PARTNER_ORDER_ID, String.valueOf(paymentParams.getOrderId()));
		parameters.put(PARTNER_USER_ID, PARTNER);
		parameters.put(ITEM_NAME, paymentParams.getItemName());
		parameters.put(QUANTITY, String.valueOf(paymentParams.getQuantity()));
		parameters.put(TOTAL_AMOUNT, String.valueOf(paymentParams.getTotalAmount()));
		parameters.put(TAX_FREE_AMOUNT, "0");
		parameters.put(CANCEL_URL, CANCEL_URI);
		parameters.put(FAIL_URL, FAIL_URI);

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
