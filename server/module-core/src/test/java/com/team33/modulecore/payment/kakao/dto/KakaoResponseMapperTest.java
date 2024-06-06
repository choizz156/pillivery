package com.team33.modulecore.payment.kakao.dto;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiApproveResponse;
import com.team33.moduleexternalapi.dto.kakao.KakaoApiPayLookupResponse;

class KakaoResponseMapperTest {

	@DisplayName("mapper로 dto를 변환할 수 있다.")
	@Test
	void test() throws Exception{
		//given
		KakaoApiPayLookupResponse sample = FixtureMonkeyFactory.get().giveMeBuilder(KakaoApiPayLookupResponse.class).sample();
		//when
		KakaoLookupResponse kakaoLookupResponse =
			KakaoResponseMapper.INSTANCE.toKakaoCoreLookupResponse(sample);
		//then
		assertThat(kakaoLookupResponse).usingRecursiveComparison().isEqualTo(sample);
	}

	@Test
	void test2() throws Exception{
		//given
		KakaoApiApproveResponse sample = FixtureMonkeyFactory.get()
			.giveMeBuilder(KakaoApiApproveResponse.class)
			.sample();
		//when
		KakaoApproveResponse kakaoCoreApproveResponse =
			KakaoResponseMapper.INSTANCE.toKakaoCoreApproveResponse(
			sample);
		//then
		assertThat(kakaoCoreApproveResponse).usingRecursiveComparison().isEqualTo(sample);
	}
}