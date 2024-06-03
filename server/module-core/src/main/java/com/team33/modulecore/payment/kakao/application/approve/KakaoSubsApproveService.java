package com.team33.modulecore.payment.kakao.application.approve;

import org.springframework.stereotype.Service;

import com.team33.modulecore.order.domain.entity.Order;
import com.team33.modulecore.payment.application.approve.SubscriptionApprove;
import com.team33.modulecore.payment.application.approve.SubscriptionApproveService;
import com.team33.modulecore.payment.dto.ApproveRequest;
import com.team33.modulecore.payment.kakao.dto.KakaoApiResponseMapper;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveOneTimeRequest;
import com.team33.modulecore.payment.kakao.dto.KakaoApproveResponse;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoSubsApproveService implements SubscriptionApproveService<KakaoApproveResponse> {

	private final KakaoFirstSubsApprove kakaoFirstSubsApprove;
	private final SubscriptionApprove<KakaoApiApproveResponse> subscriptionApprove;

	@Override
	public KakaoApproveResponse approveFirstTime(ApproveRequest approveRequest) {
		KakaoApproveOneTimeRequest request = (KakaoApproveOneTimeRequest) approveRequest;

		// doKakaoScheduling(orderId);
		KakaoApiApproveResponse response= kakaoFirstSubsApprove.approveFirstSubscription(request);
		return KakaoApiResponseMapper.INSTANCE.toKakaoCoreApproveResponse(response);
	}

	@Override
	public KakaoApproveResponse approveSubscribe(Order order) {
		KakaoApiApproveResponse response = subscriptionApprove.approveSubscription(order);
		return KakaoApiResponseMapper.INSTANCE.toKakaoCoreApproveResponse(response);
	}
}
