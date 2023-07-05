package team33.modulecore.domain.category.repository;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import team33.modulecore.domain.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCategoryName(String categoryName);

}
