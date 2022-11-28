import { useState } from 'react';
import styled, { css } from 'styled-components';
import { loadTossPayments } from '@tosspayments/payment-sdk';
import { LightPurpleButton } from '../Buttons/PurpleButton';
import PayPageContainer from './PayPageContainer';
import Kakao from '../../assets/images/social/kakao.png';

export default function PayMethod() {
	const clientKey = process.env.REACT_APP_CLIENT_API_KEY;

	const tossPay = () =>
		loadTossPayments(clientKey).then((tossPayments) => {
			// 카드 결제 메서드 실행
			tossPayments.requestPayment('카드', {
				amount: 123223, // 가격
				orderId: `9abcdef`, // 주문 id
				orderName: '영양제', // 결제상품 이름
				customerName: `도현수`, // 판매자, 판매처 이름
				successUrl: 'http://localhost:3000', // 성공시 리다이렉트 주소
				failUrl: 'http://localhost:3000', // 실패시 리다이렉트 주소
				validHours: 24, // 유효시간
				cashReceipt: {
					type: '소득공제',
				},
			});
		});

	return (
		<PayPageContainer>
			<PayMethodHeading>결제 수단</PayMethodHeading>
			<ButtonBox>
				<LightPurpleButton
					width="200px"
					height="50px"
					onClick={tossPay}
					borderRadius="6px"
					fontSize="16px"
					fontWeight="regular"
				>
					카드 결제
				</LightPurpleButton>
				<KakaoPayButton>
					<KakaoPayImg />
					카카오페이
				</KakaoPayButton>
			</ButtonBox>
		</PayPageContainer>
	);
}

const PayMethodHeading = styled.h2`
	font-size: 20px;
	color: var(--gray-500);
	margin-bottom: 44px;
`;

const ButtonBox = styled.div`
	display: flex;
	flex-direction: row;
	width: 100%;
	justify-content: space-between;
`;

const KakaoPayButton = styled.button`
	display: flex;
	align-items: center;
	justify-content: center;
	background-color: #ffeb00;
	width: 200px;
	height: 50px;
	border-radius: 6px;
	border: none;
	font-size: 16px;
	font-weight: var(--regular);
	color: rgba(0, 0, 0 0.85);
	cursor: pointer;
	padding-right: 15px;
	transition: 0.3s all;
	&:hover {
		font-weight: var(--bold);
		background-color: #ffdb0d;
	}
`;
const KakaoPayImg = styled.img.attrs({
	src: Kakao,
})`
	height: 40px;
	width: 40px;
	padding: 9px 7px 7px 8px;
`;
