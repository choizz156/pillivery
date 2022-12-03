import styled from 'styled-components';
import { useLocation } from 'react-router-dom';
import { useState, useEffect } from 'react';
import CartList from '../../components/Lists/MyPageLists/CartList';
import { PurpleButton } from '../../components/Buttons/PurpleButton';
import Price from '../../components/Etc/Price';
import { useGet, usePost } from '../../hooks/useFetch';
import { LoadingSpinner } from '../../components/Etc/LoadingSpinner';

// 정기 장바구니
function SubCart() {
	const { pathname } = useLocation();

	const {
		isLoading,
		isError,
		data: items,
		error,
	} = useGet(
		'http://ec2-43-201-37-71.ap-northeast-2.compute.amazonaws.com:8080/carts?subscription=true',
		pathname,
	);
	// console.log('items', items);

	const { mutate } = usePost(
		'http://ec2-43-201-37-71.ap-northeast-2.compute.amazonaws.com:8080/orders?subscription=true',
	);

	if (isLoading) {
		return <LoadingSpinner />;
	}
	if (isError) {
		return <List> {error.message} </List>;
	}

	return (
		<Box>
			<List>
				{items.data.data.itemCarts.data.map((item) => (
					<CartList key={item.item.itemId} data={item} item={item.item} sub />
				))}
			</List>
			<Bottom>
				<Display>
					<Text>합계</Text>
					<Price
						nowPrice={items.data.data.totalPrice}
						fontSize="24px"
						fontWeight="bold"
					/>
					<Text>할인 금액</Text>
					<Price
						nowPrice={items.data.data.totalDiscountPrice}
						fontSize="24px"
						fontWeight="bold"
					/>
					<Text>결제 예정 금액</Text>
					<Price
						nowPrice={items.data.data.expectPrice}
						fontSize="24px"
						fontWeight="bold"
						purple
					/>
				</Display>
				<Button>
					<PurpleButton width="143px" height="50px" onClick={() => mutate()}>
						구매하기
					</PurpleButton>
				</Button>
			</Bottom>
		</Box>
	);
}

const Box = styled.div`
	width: 100%;
	height: 100%;
	display: flex;
	justify-content: center;
	align-items: center;
	flex-direction: column;
`;

const List = styled.div`
	background-color: white;
	border-radius: 10px;
	box-shadow: 0px 1px 4px rgba(0, 0, 0, 0.05);
	margin-top: 20px;
	width: 983px;
	flex-direction: column;
	display: flex;
	align-items: center;

	& > {
		:last-child {
			border: none;
		}
	}
`;

const Bottom = styled.div`
	margin-top: 74px;
	width: 864px;
	height: 126px;
	flex-direction: column;
	display: flex;
	justify-content: center;
	align-items: center;
`;

const Display = styled.div`
	border-bottom: 1px solid var(--gray-200);
	width: 670px;
	height: 40px;
	display: flex;
	align-items: center;
	justify-content: space-between;
`;

const Text = styled.div`
	font-size: 16px;
	font-weight: var(--regular);
	color: var(--gray-400);
`;

const Button = styled.div`
	margin-top: 36px;
	height: 50px;
	display: flex;
	justify-content: space-between;
	align-items: center;
`;

export default SubCart;
