/* eslint-disable jsx-a11y/label-has-associated-control */
import { useState } from 'react';
import styled, { css } from 'styled-components';

export default function AuthInput({ Ref, onKeyUp, label }) {
	const [value, setValue] = useState('');

	const onChange = (event) => {
		const { value: newValue } = event.target;
		setValue(newValue);
	};

	return (
		<InputBox isFilled={!!value}>
			<input
				ref={Ref}
				id="input"
				type="text"
				onKeyUp={onKeyUp}
				value={value}
				onChange={onChange}
			/>
			<label htmlFor="input" className="placeholder">
				{label}
			</label>
			{/* <ErrorSpan>이메일 형식으로 입력해주세요.</ErrorSpan> */}
		</InputBox>
	);
}

const InputBox = styled.div`
	width: 100%;
	position: relative;
	font-size: 18px;
	margin-top: 20px;

	& .placeholder {
		position: absolute;
		top: 50%;
		left: 0px;
		transform: translateY(-50%);
		color: var(--gray-200);
		font-size: inherit;
		cursor: text;
		transition: 0.5s ease-in-out;
		font-weight: 500;
	}
	& input[type='text'] {
		width: 100%;
		height: 40px;
		border: none;
		outline: none;
		border-bottom: 1px solid var(--gray-200);
		font-size: inherit;
	}
	& input[type='text']:focus {
		transition: 0.5s ease-in-out;
		border-bottom: 1px solid var(--purple-200);
		caret-color: var(--purple-200);
		& + .placeholder {
			color: var(--gray-300);
			font-size: 12px;
			top: -8px;
			font-weight: 300;
		}
	}
	${({ isFilled }) =>
		isFilled &&
		css`
			.placeholder {
				color: var(--gray-300);
				font-size: 12px;
				top: -8px;
				font-weight: 300;
			}
		`}
`;

const ErrorSpan = styled.span`
	display: inline-block;
	color: var(--red-100);
	font-size: 11px;
	margin-top: 5px;
`;
