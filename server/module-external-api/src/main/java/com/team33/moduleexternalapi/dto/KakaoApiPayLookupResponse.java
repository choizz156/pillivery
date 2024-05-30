package com.team33.moduleexternalapi.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoApiPayLookupResponse {
	public String tid;
	public String cid;
	public String status;
	public String partner_order_id;
	public String partner_user_id;
	public String payment_method_type;
	public String item_name;
	public int quantity;

	@JsonProperty("approved_cancel_amount")
	private ApprovedCancelAmount approvedCancelAmount;

	@JsonProperty("canceled_amount")
	private CanceledAmount canceledAmount;

	@JsonProperty("cancel_available_amount")
	private CancelAvailableAmount cancelAvailableAmount;

	public LocalDateTime created_at;
	public LocalDateTime approved_at;
	public ArrayList<PaymentActionDetail> payment_action_details;

	@NoArgsConstructor
	@Getter
	public static class Amount {
		private int total;

		@JsonProperty("tax_free")
		private int taxFree;

		private int vat;
		private int point;
		private int discount;

		@JsonProperty("green_deposit")
		private int greenDeposit;
	}

	@NoArgsConstructor
	@Getter
	public static class ApprovedCancelAmount {
		private int total;

		@JsonProperty("tax_free")
		private int taxFree;

		private int vat;
		private int point;
		private int discount;

		@JsonProperty("green_deposit")
		private int greenDeposit;
	}

	@NoArgsConstructor
	@Getter
	public static class CanceledAmount {
		private int total;

		@JsonProperty("tax_free")
		private int taxFree;

		private int vat;
		private int point;
		private int discount;

		@JsonProperty("green_deposit")
		private int greenDeposit;
	}

	@NoArgsConstructor
	@Getter
	public static class CancelAvailableAmount {
		private int total;

		@JsonProperty("tax_free")
		private int taxFree;

		private int vat;
		private int point;
		private int discount;

		@JsonProperty("green_deposit")
		private int greenDeposit;
	}

	@NoArgsConstructor
	@Getter
	public static class PaymentActionDetail {
		public String aid;
		public String payment_action_type;
		public String payment_method_type;
		public int amount;
		public int point_amount;
		public int discount_amount;
		public LocalDateTime approved_at;
		public int green_deposit;
	}
}
