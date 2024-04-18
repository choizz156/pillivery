package com.team33.modulecore.cart.repository;


import com.team33.modulecore.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

//    Cart findByUser(User user);
}
