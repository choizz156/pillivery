package com.team33.moduleexternalapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class KakaoRefundResponse {

	private String tid;

	private String cid;
	private String status;
	private String partnerOrderId;
	private String partnerUserId;
	private String paymentMethodType;
	private String itemName;
	private int quantity;
	private Amount amount;
	private ApprovedCancelAmount approvedCancelAmount;
	private CanceledAmount canceledAmount;
	private CancelAvailableAmount cancelAvailableAmount;
	private String createdAt;
	private String approvedAt;
	private String canceledAt;

	@NoArgsConstructor
	@Getter
	public static class Amount {

		private int total;

		private int taxFree;
		private int vat;
		private int point;
		private int discount;
		private int greenDeposit;

	}

	/**
	 * 취소된 금액 정보
	 */
	@NoArgsConstructor
	@Getter
	public static class ApprovedCancelAmount {

		private int total;

		private int taxFree;
		private int vat;
		private int point;
		private int discount;
		private int greenDeposit;
	}

	/**
	 * 누적 취소 금액 정보
	 */
	@NoArgsConstructor
	@Getter
	public static class CanceledAmount {

		private int total;
		private int taxFree;
		private int vat;
		private int point;
		private int discount;
		private int greenDeposit;
	}

	@NoArgsConstructor
	@Getter
	public static class CancelAvailableAmount {
		private int total;
		private int taxFree;
		private int vat;
		private int point;
		private int discount;
		private int greenDeposit;
	}
}
