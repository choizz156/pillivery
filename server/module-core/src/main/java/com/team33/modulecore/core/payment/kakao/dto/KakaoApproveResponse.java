package com.team33.modulecore.core.payment.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoApproveResponse {

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
	@AllArgsConstructor
	@Builder
	@Data
	public static class Amount {

		private int total;
		private int tax_free;
		private int vat;
		private int discount;
	}
}
