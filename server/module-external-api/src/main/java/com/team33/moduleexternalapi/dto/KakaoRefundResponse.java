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
	private Integer quantity;
	private Amount amount;
	private ApprovedCancelAmount approvedCancelAmount;
	private CanceledAmount canceledAmount;
	private CancelAvailableAmount cancelAvailableAmount;
	private String createdAt;
	private String approvedAt;
	private String canceledAt;

	@NoArgsConstructor
	@Getter
	private static class Amount {

		private Integer total;
		private Integer taxFree;
		private Integer vat;
		private Integer point;
		private Integer discount;
		private Integer greenDeposit;

	}

	@NoArgsConstructor
	@Getter
	private static class ApprovedCancelAmount {

		private Integer total;
		private Integer taxFree;
		private Integer vat;
		private Integer point;
		private Integer discount;
		private Integer greenDeposit;
	}

	@NoArgsConstructor
	@Getter
	private static class CanceledAmount {
		private Integer total;
		private Integer taxFree;
		private Integer vat;
		private Integer point;
		private Integer discount;
		private Integer greenDeposit;
	}

	@NoArgsConstructor
	@Getter
	private static class CancelAvailableAmount {
		private Integer total;
		private Integer taxFree;
		private Integer vat;
		private Integer point;
		private Integer discount;
		private Integer greenDeposit;
	}
}
