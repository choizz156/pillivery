package com.team33.modulecore.core.payment.kakao.application.approve;

import org.springframework.stereotype.Service;

import com.team33.modulecore.core.payment.domain.approve.OneTimeApprove;
import com.team33.modulecore.core.payment.domain.approve.OneTimeApproveService;
import com.team33.modulecore.core.payment.domain.dto.ApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveRequest;
import com.team33.modulecore.core.payment.kakao.dto.KakaoApproveResponse;
import com.team33.modulecore.core.payment.kakao.dto.KakaoResponseMapper;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoOneTimeApproveService implements OneTimeApproveService<KakaoApproveResponse> {

	private final OneTimeApprove<KakaoApiApproveResponse, KakaoApproveRequest> oneTimeApprove;

	@Override
	public KakaoApproveResponse approveOneTime( ApproveRequest approveRequest) {
		KakaoApproveRequest request = (KakaoApproveRequest) approveRequest;

		KakaoApiApproveResponse response = oneTimeApprove.approveOneTime(request);

		return KakaoResponseMapper.INSTANCE.toKakaoCoreApproveResponse(response);
	}
}
