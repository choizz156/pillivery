package com.team33.modulecore.payment.kakao.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoLookupResponse {
	public String tid;
	public String cid;
	public String status;
	public String partner_order_id;
	public String partner_user_id;
	public String payment_method_type;
	public String item_name;
	public int quantity;

	private ApprovedCancelAmount approvedCancelAmount;

	private CanceledAmount canceledAmount;

	private CancelAvailableAmount cancelAvailableAmount;

	public LocalDateTime created_at;
	public LocalDateTime approved_at;
	public List<PaymentActionDetail> payment_action_details;

	@Data
	@NoArgsConstructor
	private static class Amount {
		private int total;

		private int taxFree;

		private int vat;
		private int point;
		private int discount;

		@JsonProperty("green_deposit")
		private int greenDeposit;
	}

	@Data
	@NoArgsConstructor
	public static class ApprovedCancelAmount {
		private int total;

		private int taxFree;

		private int vat;
		private int point;
		private int discount;

		@JsonProperty("green_deposit")
		private int greenDeposit;
	}

	@Data
	@NoArgsConstructor
	public static class CanceledAmount {
		private int total;

		private int taxFree;

		private int vat;
		private int point;
		private int discount;

		@JsonProperty("green_deposit")
		private int greenDeposit;
	}

	@Data
	@NoArgsConstructor
	public static class CancelAvailableAmount {
		private int total;

		private int taxFree;

		private int vat;
		private int point;
		private int discount;

		@JsonProperty("green_deposit")
		private int greenDeposit;
	}

	@Data
	@NoArgsConstructor
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
