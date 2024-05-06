//package com.team33.modulecore.category.domain.repository;
//
//import static com.team33.modulecore.category.domain.CategoryName.BONE;
//import static com.team33.modulecore.category.domain.CategoryName.EYE;
//import static org.assertj.core.api.Assertions.assertThat;
//
//import com.team33.modulecore.category.domain.Category;
//import java.util.List;
//import java.util.stream.Stream;
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.Persistence;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.junit.jupiter.api.TestInstance.Lifecycle;
//
//
//@TestInstance(Lifecycle.PER_CLASS)
//class CategoryQueryRepositoryTest {
//
//    private EntityManagerFactory emf;
//    private EntityManager em;
//    private CategoryQueryRepository categoryQueryRepository;
//
//    @BeforeAll
//    void beforeAll() {
//        emf = Persistence.createEntityManagerFactory("test");
//        em = emf.createEntityManager();
//        categoryQueryRepository = new CategoryQueryRepositoryImpl(em);
//        em.getTransaction().begin();
//        Stream.of(EYE, BONE).map(Category::of).forEach(em::persist);
//    }
//
//    @AfterAll
//    void afterAll() {
//        em.getTransaction().rollback(); // 커넥션 반납용 롤백
//        em.close();
//        emf.close();
//    }
//
//    @DisplayName("카테고리 네임 목록을 통해 카테고리 객체를 조회할 수 있다.")
//    @Test
//    void 카테고리_조회1() throws Exception {
//        //given//when
//        List<Category> categories =
//            categoryQueryRepository.findCategoriesByCategoryName(List.of(EYE, BONE));
//
//        //then
//        assertThat(categories).hasSize(2)
//            .extracting("categoryName")
//            .contains(EYE, BONE);
//    }
//
//    @DisplayName("카테고리 네임 목록을 통해 카테고리 객체를 조회할 수 있다.")
//    @Test
//    void 카테고리_조회2() throws Exception {
//        //given//when
//        Category category =
//            categoryQueryRepository.findByCategoryName(EYE).orElse(Category.of(BONE));
//
//        //then
//        assertThat(category.getCategoryName()).isEqualByComparingTo(EYE);
//    }
//}