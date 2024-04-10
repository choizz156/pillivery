package com.team33.modulecore.domain.cart.repository;


import com.team33.modulecore.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

//    Cart findByUser(User user);
}
