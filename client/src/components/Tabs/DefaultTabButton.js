import React, { useCallback, useRef, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import styled from 'styled-components';

// currentIdx ==> 지금 선택한 탭의 index
// highlightValue ==> 지금 선택한 탭의 left 위치 (0번째: 0, 1번째: 68, 2번째: 136 ... => 68씩 증가!)
function DefaultTabButton({
	menuArr,
	delayButton,
	toggle,
	fontSize = '16px',
	currentIdx,
	highlightLeftValue,
}) {
	const { pathname } = useLocation();
	const [currentTab, setCurrentTab] = useState(currentIdx || 0);
	const [highlight, setHighlight] = useState({
		left: highlightLeftValue || 0,
		width: 68,
	});

	const [delayOpen, setDelayOpen] = useState(false);
	const menuEl = useRef(null);
	const navigate = useNavigate();

	const splitedPath = pathname.split('/');
	// /normal, /subscription을 제외한 path
	const joinPath = splitedPath.slice(0, splitedPath.length - 1).join('/');

	const handleBtnClick = useCallback((e) => {
		const index = Number(e.target.id);
		setCurrentTab(index);
		// 선택된 Tab Menu에 따라 하이라이트가 이동
		const left = menuEl.current.children[index].offsetLeft;
		const width = menuEl.current.children[index].offsetWidth;
		console.log(`left는 ${left}, width는 ${width}`);
		switch (index) {
			case 0:
				setHighlight({
					left,
					width,
				});
				break;
			case 1:
				setHighlight({
					left,
					width,
				});
				break;
			case 2:
				setHighlight({
					left,
					width,
				});
				break;
			case 3:
				setHighlight({
					left,
					width,
				});
				break;
			default:
				break;
		}

		// 일반/정기 토글 클릭 시 해당 페이지의 일반/정기 페이지로 이동
		if (toggle) {
			if (index === 1) {
				navigate(`${joinPath}/subscription`);
			} else if (index === 0) {
				navigate(`${joinPath}/normal`);
			}
		}
	}, []);

	return (
		<TabContainer toggle={toggle}>
			<TabMenu ref={menuEl} fontSize={fontSize}>
				{menuArr.map(({ name, index }) => (
					<li
						key={`${index}-${name}`}
						id={index}
						className={currentTab === index ? 'submenu focused' : 'submenu'}
						onClick={handleBtnClick}
						role="presentation"
					>
						{name}
					</li>
				))}
				{delayButton && (
					<li
						className={`submenu red ${delayOpen ? 'open' : ''}`}
						onClick={() => setDelayOpen((prev) => !prev)}
						role="presentation"
					>
						미루기
					</li>
				)}
			</TabMenu>
			<Highlight
				left={highlight.left}
				width={highlight.width}
				toggle={toggle}
			/>
		</TabContainer>
	);
}

export default React.memo(DefaultTabButton);

const TabContainer = styled.div`
	background-color: ${({ toggle }) => (toggle ? 'white' : 'var(--gray-100)')};
	position: relative;
	width: fit-content;
	padding-left: 8px;
	padding-right: 8px;
	box-shadow: 0px 1px 8px rgba(0, 0, 0, 0.07);
	border-radius: 50px;
`;

const TabMenu = styled.ul`
	display: flex;
	justify-items: center;
	align-items: center;
	list-style: none;
	cursor: pointer;
	z-index: 1;
	position: relative;

	.submenu {
		font-size: ${(props) => props.fontSize};
		color: var(--gray-300);
		text-align: center;
		padding: 20px 20px;
		cursor: pointer;
		transition: all 0.4s ease-in-out;
	}

	.focused {
		color: var(--gray-500);
		transition: all 0.4s ease-in-out;
	}

	.red {
		color: var(--red-100);
	}

	.open {
		box-shadow: 0px 1px 8px rgba(0, 0, 0, 0.07);
		background: white;
		border-radius: 50px;
		padding: 14px 20px;
	}
`;

const Highlight = styled.span`
	background: ${({ toggle }) => (toggle ? 'var(--gray-100)' : 'white')};
	position: absolute;
	width: ${(props) => props.width}px;
	top: 6px;
	bottom: 6px;
	transition: 0.4s cubic-bezier(0.65, 0, 0.35, 1);
	left: ${(props) => props.left + 8}px;
	/* transform: translateX(${({ left }) => left}px); */
	border-radius: 50px;
	box-shadow: ${({ toggle }) => (toggle ? 'inset' : '')} 0px 1px 8px
		rgba(0, 0, 0, 0.07);
`;
