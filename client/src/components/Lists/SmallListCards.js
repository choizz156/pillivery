/* eslint-disable no-nested-ternary */
import styled, { css } from 'styled-components';
import Price from '../Etc/Price';
import { ShortTextStar } from '../Stars/TextStar';
// 세연님이 별점 만들면 가져다가 쓰자.

function SmallListCards({ item }) {
	console.log(item, 'item임/');
	return (
		<EntireContainer id="이거임">
			<DefaultContainer>
				<ContentBox>
					<ContentContainer />
					<ContentContainer middle>
						<ItemImg src={item.thumbnail} alt="상품 이미지" />
					</ContentContainer>
					<ContentContainer buttom>
						<div className="title brandName">{item.brand}</div>
						<div className="title itemName">{item.title}</div>
						<Price
							nowPrice={8000}
							// item.price
							beforePrice={10000}
							// item.discountPrice
							discountRate="20%"
							// item.discountRate
							fontSize="16px"
							font-weight="var(--regular)"
						/>
					</ContentContainer>
				</ContentBox>
			</DefaultContainer>
			<DefaultContainer className="hover" hover>
				<ContentBox>
					<ContentContainer star>
						<ShortTextStar />
					</ContentContainer>
					<ContentContainer middle>
						<ItemDescription>{item.content}</ItemDescription>
					</ContentContainer>
				</ContentBox>
			</DefaultContainer>
		</EntireContainer>
	);
}

const EntireContainer = styled.div`
	cursor: pointer;
	display: inline-flex;
	position: relative;
	margin-right: 20px;
	margin-bottom: 30px;
	/* background-color: white; */
	/* border: 1px solid red; // 구분을 쉽게 하기 위한 선입니다. */
	&:hover {
		.hover {
			opacity: 1;
		}
		.title {
			color: white;
		}
		.brandName {
			color: var(--gray-200);
		}
		.beforeDiscounted {
			color: var(--gray-200);
		}
	}
`;

const DefaultContainer = styled.div`
	width: 245px;
	height: 387px;
	border-radius: 10px;
	box-shadow: 0px 1px 8px rgba(0, 0, 0, 0.07);
	transition: 0.25s;
	background-color: white;
	${(props) =>
		props.hover // hover라는 프롭스가 들어간 디폴트 컨테이너
			? css`
					position: absolute;
					top: 0px;
					background-color: rgba(0, 0, 0, 0.4);
					backdrop-filter: blur(2px);
					opacity: 0;
					&:hover {
						opacity: 1;
					}
			  `
			: null}
`;
const ContentBox = styled.div`
	width: 245px;
	height: 387px;
	display: flex;
	flex-direction: column;
	padding: 24px 24px 33px 24px;
`;
const ContentContainer = styled.div`
	display: flex;
	flex-direction: row-reverse;
	margin-top: 10px;
	${(props) =>
		props.middle
			? css`
					padding-top: 23px;
					justify-content: center;
					padding-bottom: 77px;
			  `
			: props.buttom
			? css`
					z-index: 1;
					flex-direction: column;
					justify-content: none;
			  `
			: props.star
			? css`
					flex-direction: row;
					margin-top: 5px;
			  `
			: null}

	.brandName {
		font-size: 13px;
		color: var(--gray-400);
		padding-bottom: 10.5px;
	}
	.itemName {
		font-weight: var(--extraBold);
		font-size: 16px;
		padding-bottom: 27.5px;
	}
	.itemPrice {
		font-size: 16px;
		font-weight: var(--regular);
	}
`;

const ItemImg = styled.img`
	width: 110px;
	height: 125px;
`;

const ItemDescription = styled.p`
	color: white;
	font-size: 20px;
	line-height: 25px;
	letter-spacing: -0.04em;
`;

export default SmallListCards;
