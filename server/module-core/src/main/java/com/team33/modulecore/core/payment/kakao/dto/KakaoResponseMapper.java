package com.team33.modulecore.core.payment.kakao.dto;

import static org.mapstruct.factory.Mappers.*;

import org.mapstruct.Mapper;

import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiPayLookupResponse;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiRequestResponse;

@Mapper(componentModel = "spring")
public interface KakaoResponseMapper {
	KakaoResponseMapper INSTANCE = getMapper(KakaoResponseMapper.class);

	KakaoLookupResponse toKakaoCoreLookupResponse(KakaoApiPayLookupResponse response);
	KakaoRequestResponse toKakaoCoreRequestResponse(KakaoApiRequestResponse response);
	KakaoApproveResponse toKakaoCoreApproveResponse(KakaoApiApproveResponse response);
}
