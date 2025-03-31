package com.team33.moduleexternalapi.dto.kakao;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class KakaoApiApproveResponse {

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
	private CardInfo card_info;

	@NoArgsConstructor
	@Getter
	public static class Amount {

		private int total;
		private int tax_free;
		private int vat;
		private int discount;
	}

	@NoArgsConstructor
	@Getter
	public static class CardInfo {
		private String interest_free_install;
		private String bin;
		private String card_type;
		private String card_mid;
		private String approved_id;
		private String install_month;
		private String installment_type;
		private String kakaopay_purchase_corp;
		private String kakaopay_purchase_corp_code;
		private String kakaopay_issuer_corp;
		private String kakaopay_issuer_corp_code;

		@Builder
		public CardInfo(
			String interest_free_install,
			String bin,
			String card_type,
			String card_mid,
			String approved_id,
			String install_month,
			String installment_type,
			String kakaopay_purchase_corp,
			String kakaopay_purchase_corp_code,
			String kakaopay_issuer_corp,
			String kakaopay_issuer_corp_code
		) {
			this.interest_free_install = interest_free_install;
			this.bin = bin;
			this.card_type = card_type;
			this.card_mid = card_mid;
			this.approved_id = approved_id;
			this.install_month = install_month;
			this.installment_type = installment_type;
			this.kakaopay_purchase_corp = kakaopay_purchase_corp;
			this.kakaopay_purchase_corp_code = kakaopay_purchase_corp_code;
			this.kakaopay_issuer_corp = kakaopay_issuer_corp;
			this.kakaopay_issuer_corp_code = kakaopay_issuer_corp_code;
		}
	}

	@Builder
	public KakaoApiApproveResponse(
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
		int quantity,
		CardInfo card_info
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
		this.card_info = card_info;
	}
}
