package com.team33.moduleexternalapi.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class KakaoApiCancelResponse {

	private String tid;
	private String cid;
	private String status;

	@JsonProperty("partner_order_id")
	private String partnerOrderId;

	@JsonProperty("partner_user_id")
	private String partnerUserId;

	@JsonProperty("payment_method_type")
	private String paymentMethodType;

	@JsonProperty("item_name")
	private String itemName;

	private int quantity;
	private Amount amount;

	@JsonProperty("approved_cancel_amount")
	private ApprovedCancelAmount approvedCancelAmount;

	@JsonProperty("canceled_amount")
	private CanceledAmount canceledAmount;

	@JsonProperty("cancel_available_amount")
	private CancelAvailableAmount cancelAvailableAmount;

	@JsonProperty("created_at")
	private LocalDateTime createdAt;

	@JsonProperty("approved_at")
	private LocalDateTime approvedAt;

	@JsonProperty("canceled_at")
	private LocalDateTime canceledAt;

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
}
