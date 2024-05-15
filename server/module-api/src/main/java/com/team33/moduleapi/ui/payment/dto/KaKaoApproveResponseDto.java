package com.team33.moduleapi.ui.payment.dto;

import com.team33.modulecore.payment.kakao.dto.KaKaoApproveResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
public class KaKaoApproveResponseDto {

	private String item_name;
	private String item_code;
	private String created_at;
	private String approved_at;
	private String payload;
	private Amount amount;
	private int quantity;

	@Getter
	private static class Amount {

		private int total;

		private int tax_free;
		private int vat;
		private int discount;

		public Amount(int total, int tax_free, int vat, int discount) {
			this.total = total;
			this.tax_free = tax_free;
			this.vat = vat;
			this.discount = discount;
		}

	}

	@Builder
	private KaKaoApproveResponseDto(
		String item_name,
		String item_code,
		String created_at,
		String approved_at,
		String payload,
		Amount amount,
		int quantity
	) {
		this.item_name = item_name;
		this.item_code = item_code;
		this.created_at = created_at;
		this.approved_at = approved_at;
		this.payload = payload;
		this.amount = amount;
		this.quantity = quantity;
	}

	public static KaKaoApproveResponseDto from(
		KaKaoApproveResponse approveResponse
	) {
		return KaKaoApproveResponseDto.builder()
			.item_name(approveResponse.getItem_name())
			.item_code(approveResponse.getItem_code())
			.created_at(approveResponse.getCreated_at())
			.approved_at(approveResponse.getApproved_at())
			.payload(approveResponse.getPayload())
			.amount(
				new Amount(
					approveResponse.getAmount().getTotal(),
					approveResponse.getAmount().getTax_free(),
					approveResponse.getAmount().getVat(),
					approveResponse.getAmount().getDiscount()
				)
			)
			.quantity(approveResponse.getQuantity())
			.build();
	}

}
