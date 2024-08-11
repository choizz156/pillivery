package com.team33.modulecore.core.cart.application;

class CartKeySupplier {
	public static String from(long cartId) {
		return "mem:cartId : " + cartId;
	}
}
