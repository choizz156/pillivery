package com.team33.moduleexternalapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class KaKaoApproveResponse {

	private String aid;
	private String tid;
	private String cid;
	private String sid;
	private String partner_order_id;
	private String partner_user_id;
	private String payment_method_type;
	private String item_name;
	private String item_code;
	private String created_at;
	private String approved_at;
	private String payload;
	private Amount amount;
	private int quantity;

	@NoArgsConstructor
	@Getter
	public static class Amount {

		private int total;
		private int tax_free;
		private int vat;
		private int discount;
	}

	@Builder
	public KaKaoApproveResponse(
		String aid,
		String tid,
		String cid,
		String sid,
		String partner_order_id,
		String partner_user_id,
		String payment_method_type,
		String item_name,
		String item_code,
		String created_at,
		String approved_at,
		String payload,
		Amount amount,
		int quantity
	) {
		this.aid = aid;
		this.tid = tid;
		this.cid = cid;
		this.sid = sid;
		this.partner_order_id = partner_order_id;
		this.partner_user_id = partner_user_id;
		this.payment_method_type = payment_method_type;
		this.item_name = item_name;
		this.item_code = item_code;
		this.created_at = created_at;
		this.approved_at = approved_at;
		this.payload = payload;
		this.amount = amount;
		this.quantity = quantity;
	}
}
