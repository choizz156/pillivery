package com.team33.modulecore.payment.kakao.application.approve;

import org.springframework.stereotype.Service;

import com.team33.modulecore.payment.application.approve.NormalApprove;
import com.team33.modulecore.payment.application.approve.NormalApproveService;
import com.team33.modulecore.payment.dto.ApproveRequest;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.moduleexternalapi.dto.KakaoApproveResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoNormalApproveService implements NormalApproveService<KakaoApproveResponse> {

	private final NormalApprove<KakaoApproveResponse, KakaoApproveOneTimeRequest> normalApprove;

	@Override
	public KakaoApproveResponse approveOneTime( ApproveRequest approveRequest) {
		KakaoApproveOneTimeRequest request = (KakaoApproveOneTimeRequest) approveRequest;

		return normalApprove.approveOneTime(request);
	}
}
