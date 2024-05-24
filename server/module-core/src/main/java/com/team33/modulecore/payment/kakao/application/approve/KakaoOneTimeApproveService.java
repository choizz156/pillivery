package com.team33.modulecore.payment.kakao.application.approve;

import org.springframework.stereotype.Service;

import com.team33.modulecore.payment.application.approve.OneTimeApprove;
import com.team33.modulecore.payment.application.approve.OneTimeApproveService;
import com.team33.modulecore.payment.dto.ApproveRequest;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.moduleexternalapi.dto.KakaoApproveResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoOneTimeApproveService implements OneTimeApproveService<KakaoApproveResponse> {

	private final OneTimeApprove<KakaoApproveResponse, KakaoApproveOneTimeRequest> oneTimeApprove;

	@Override
	public KakaoApproveResponse approveOneTime( ApproveRequest approveRequest) {
		KakaoApproveOneTimeRequest request = (KakaoApproveOneTimeRequest) approveRequest;

		return oneTimeApprove.approveOneTime(request);
	}
}
