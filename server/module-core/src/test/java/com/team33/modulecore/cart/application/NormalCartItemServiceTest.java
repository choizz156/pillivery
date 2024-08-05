package com.team33.modulecore.cart.application;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.core.cart.application.CommonCartItemService;
import com.team33.modulecore.core.cart.application.NormalCartItemService;
import com.team33.modulecore.core.cart.domain.entity.CartItem;
import com.team33.modulecore.core.cart.domain.entity.NormalCart;
import com.team33.modulecore.cart.mock.FakeCartRepository;
import com.team33.modulecore.core.item.domain.entity.Item;

class NormalCartItemServiceTest {

	private FakeCartRepository cartRepository;
	private NormalCart normalCart;
	private Item item;

	@BeforeEach
	void setUp() {
		normalCart = FixtureMonkeyFactory.get().giveMeBuilder(NormalCart.class)
			.set("id", 1L)
			.set("price", null)
			.set("cartItems", new ArrayList<>())
			.sample();

		item = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.set("id", 1L)
			.set("information.price.realPrice", 1000)
			.set("information.price.discountPrice", 500)
			.sample();

		cartRepository = new FakeCartRepository();
		cartRepository.save(normalCart);
	}

	@AfterEach
	void tearDown() {
		cartRepository.deleteById(1L);
	}

	@DisplayName("일반 아이템을 장바구니에 넣을 수 있다.")
	@Test
	void 장바구니_추가() throws Exception {
		//given
		NormalCartItemService normalCartItemService = new NormalCartItemService(cartRepository);

		//when
		normalCartItemService.addItem(normalCart.getId(), item, 3);

		//then
		assertThat(normalCart.getCartItems()).hasSize(1)
			.extracting("item.id")
			.containsOnly(1L);
	}

	@DisplayName("일반 아이템을 장바구니에서 뺄 수 있다.")
	@Test
	void 장바구니_제거() throws Exception {
		//given
		NormalCartItemService normalCartItemService = new NormalCartItemService(cartRepository);
		normalCartItemService.addItem(normalCart.getId(), item, 3);

		CommonCartItemService cartItemService = new CommonCartItemService(cartRepository);

		//when
		cartItemService.removeCartItem(normalCart.getId(), 1L);

		//then
		assertThat(normalCart.getCartItems()).hasSize(0);
		assertThat(normalCart.getTotalItemCount()).isEqualTo(0);
		assertThat(normalCart.getTotalDiscountPrice()).isEqualTo(0);
		assertThat(normalCart.getExpectedPrice()).isEqualTo(0);
	}

	@DisplayName("장바구니의 담겨져 있는 수량을 변경할 수 있다.")
	@Test
	void 수량_변경() throws Exception {
		//given
		NormalCartItemService normalCartItemService = new NormalCartItemService(cartRepository);
		normalCartItemService.addItem(normalCart.getId(), item, 3);

		CommonCartItemService cartItemService = new CommonCartItemService(cartRepository);

		//when
		cartItemService.changeQuantity(normalCart.getId(), 1L, 5);

		//then
		assertThat(normalCart.getCartItems()).hasSize(1)
			.extracting("totalQuantity")
			.containsOnly(5);
		assertThat(normalCart.getTotalItemCount()).isEqualTo(5);
		assertThat(normalCart.getTotalDiscountPrice()).isEqualTo(2500);
		assertThat(normalCart.getExpectedPrice()).isEqualTo(2500);
	}

	@DisplayName("구매 수량 변경 시 기존 수량과 동일하면 아무일도 일어나지 않는다.")
	@Test
	void 수량_변경_예외() throws Exception {
		//given
		NormalCartItemService normalCartItemService = new NormalCartItemService(cartRepository);
		normalCartItemService.addItem(normalCart.getId(), item, 3);

		CommonCartItemService cartItemService = new CommonCartItemService(cartRepository);

		//when
		cartItemService.changeQuantity(normalCart.getId(), 1L, 3);

		//then
		assertThat(normalCart.getCartItems()).hasSize(1)
			.extracting("totalQuantity")
			.containsOnly(3);
	}

	@DisplayName("구매 완료 후 구매한 상품을 장바구니에서 제거한다.")
	@Test
	void 구매상품_장바구니_제거() throws Exception {
		//given
		CommonCartItemService cartItemService = new CommonCartItemService(cartRepository);
		CartItem cartItem = CartItem.of(item, 3);
		normalCart.addNormalItem(cartItem);

		//when
		cartItemService.refresh(normalCart.getId(), List.of(1L));

		//then
		assertThat(normalCart.getCartItems()).hasSize(0);
	}
}