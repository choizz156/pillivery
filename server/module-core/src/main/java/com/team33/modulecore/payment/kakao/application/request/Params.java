package com.team33.modulecore.payment.kakao.application.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Params {
	PARTNER("pillivery"),
	ONE_TIME_APPROVAL_URL ( "http://localhost:8080/payme(/approve"),
	SUBSCRIPTION_APPROVAL_URI ( "http://localhost:8080/payments/appr(/subscription"),
	CANCEL_URI ( "http://localhost:8080/payme(/cancel"),
	FAIL_URI ( "http://localhost:8080/payme(/fail"),
	ONE_TIME_CID("TC0ONETIME"),
	SUBSCRIP_CID("TCSUBSCRIP"),
	CID("cid"),
	APPROVAL_URL("approval_url"),
	SID("sid"),
	TID("tid"),
	PARTNER_ORDER_ID("partner_order_id"),
	PARTNER_USER_ID("partner_user_id"),
	PG_TOKEN("pg_token"),
	ITEM_NAME("item_name"),
	QUANTITY("quantity"),
	TOTAL_AMOUNT("total_amount"),
	TAX_FREE_AMOUNT("tax_free_amount"),
	CANCEL_URL("cancel_url"),
	FAIL_URL("fail_url"),
	CANCEL_AMOUNT("cancel_amount"),
	CANCEL_TAX_FREE_AMOUNT("cancel_tax_free_amount");

	private final String value;

}
