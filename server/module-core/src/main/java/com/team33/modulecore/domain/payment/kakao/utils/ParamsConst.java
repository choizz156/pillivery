package com.team33.modulecore.domain.payment.kakao.utils;

import static lombok.AccessLevel.PROTECTED;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
public abstract class ParamsConst {

    protected static final String PARTNER = "pillivery";
    protected static final String ONE_TIME_APPROVAL_URL = "ec2-43-201-37-71.ap-northeast-2.compute.amazonaws.com/payments/kakao/approve";
    protected static final String SUBSCRIPTION_APPROVAL_URI = "ec2-43-201-37-71.ap-northeast-2.compute.amazonaws.com/payments/kakao/subs/approve";
    protected static final String CANCEL_URI = "ec2-43-201-37-71.ap-northeast-2.compute.amazonaws.com/payments/kakao/cancel";
    protected static final String FAIL_URI = "ec2-43-201-37-71.ap-northeast-2.compute.amazonaws.com/payments/kaka0/fail";
    protected static final String ONE_TIME_CID = "TC0ONETIME";
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
    protected static final int NAME_INDEX = 0;
}
