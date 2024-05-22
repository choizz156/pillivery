package com.team33.modulecore.payment.kakao.application;

import static lombok.AccessLevel.PROTECTED;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
public abstract class ParamsConst {

    protected static final String PARTNER = "pillivery";
    protected static final String ONE_TIME_APPROVAL_URL = "http://localhost:8080/payments/approve";
    protected static final String SUBSCRIPTION_APPROVAL_URI = "http://localhost:8080/payments/approve/subscription";
    protected static final String CANCEL_URI = "http://localhost:8080/payments/cancel";
    protected static final String FAIL_URI = "http://localhost:8080/payments/fail";
    public static final String ONE_TIME_CID = "TC0ONETIME";
    protected static final String SUBSCRIP_CID = "TCSUBSCRIP";
    protected static final String CID = "cid";
    protected static final String APPROVAL_URL = "approval_url";
    protected static final String SID = "sid";
    protected static final String TID = "tid";
    protected static final String PARTNER_ORDER_ID = "partner_order_id";
    protected static final String PARTNER_USER_ID = "partner_user_id";
    protected static final String PG_TOKEN = "pg_token";
    protected static final String ITEM_NAME = "item_name";
    protected static final String QUANTITY = "quantity";
    protected static final String TOTAL_AMOUNT = "total_amount";
    protected static final String TAX_FREE_AMOUNT = "tax_free_amount";
    protected static final String CANCEL_URL = "cancel_url";
    protected static final String FAIL_URL = "fail_url";
    protected static final String CANCEL_AMOUNT = "cancel_amount";
    protected static final String CANCEL_TAX_FREE_AMOUNT = "cancel_tax_free_amount";
}
