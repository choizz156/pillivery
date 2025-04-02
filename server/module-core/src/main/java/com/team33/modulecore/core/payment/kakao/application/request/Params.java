package com.team33.modulecore.core.payment.kakao.application.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Params {

	BASE_URL("http://localhost:8080/api/payments/"),

	ONE_TIME_APPROVAL_URL(BASE_URL.value + "approve"),
	SUBSCRIPTION_APPROVAL_URL(BASE_URL.value + "approve/subscriptionsFirst"),
	CANCEL_URI(BASE_URL.value + "cancel"),
	FAIL_URI(BASE_URL.value + "fail"),

	PARTNER("pillivery"),
	ONE_TIME_CID("TC0ONETIME"),
	SUBSCRIPTION_CID("TCSUBSCRIP"),
	CID("cid"),

	APPROVAL_URL("approval_url"),
	CANCEL_URL("cancel_url"),
	FAIL_URL("fail_url"),

	SID("sid"),
	TID("tid"),
	PARTNER_ORDER_ID("partner_order_id"),
	PARTNER_USER_ID("partner_user_id"),
	PG_TOKEN("pg_token"),
	ITEM_NAME("item_name"),
	QUANTITY("quantity"),
	TOTAL_AMOUNT("total_amount"),
	TAX_FREE_AMOUNT("tax_free_amount"),

	CANCEL_AMOUNT("cancel_amount"),
	CANCEL_TAX_FREE_AMOUNT("cancel_tax_free_amount");

	private final String value;

}