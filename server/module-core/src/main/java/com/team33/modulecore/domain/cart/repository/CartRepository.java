package com.team33.modulecore.domain.cart.repository;


import com.team33.modulecore.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.team33.modulecore.domain.cart.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByUser(User user);
}
