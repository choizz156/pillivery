package team33.modulecore.domain.cart.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import team33.modulecore.domain.cart.entity.Cart;
import team33.modulecore.domain.user.entity.User;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByUser(User user);
}
