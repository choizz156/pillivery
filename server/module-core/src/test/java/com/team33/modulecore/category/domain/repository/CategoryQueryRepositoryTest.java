package com.team33.modulecore.category.domain.repository;

import static com.team33.modulecore.category.domain.CategoryName.BONE;
import static com.team33.modulecore.category.domain.CategoryName.EYE;
import static org.assertj.core.api.Assertions.assertThat;

import com.team33.modulecore.category.domain.Category;
import com.team33.modulecore.category.domain.mock.MemoryCategoryRepository;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@linkplain MemoryCategoryRepository}는 {@linkplain CategoryQueryRepositoryImpl}과 로직이 동일한 가짜 대역 리포지토리입니다.
 * {@code MemoryCategoryRepository}에서 발견된 버그나 실패한 테스트에 대한 수정 사항은,
 * 로직의 일관성을 유지하기 위해 {@code CategoryQueryRepositoryImpl}에도 반영되어야 합니다.
 */

class CategoryQueryRepositoryTest {

    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static CategoryQueryRepository categoryQueryRepository;

    @BeforeAll
    static void beforeAll() {
        emf = Persistence.createEntityManagerFactory("test");
        em = emf.createEntityManager();
        categoryQueryRepository = new MemoryCategoryRepository(em);
        em.getTransaction().begin();
    }

    @AfterAll
    static void afterAll() {
        em.getTransaction().rollback(); // 커넥션 반납용 롤백
        em.close();
        emf.close();

        em = null;
        emf = null;
    }

    @DisplayName("카테고리 네임 목록을 통해 카테고리 객체를 조회할 수 있다.")
    @Test
    void 카테고리_조회1() throws Exception {
        //given
        var testCategory = List.of(EYE, BONE);

        //when
        List<Category> categories = categoryQueryRepository.findCategoriesByCategoryName(testCategory);

        //then
        assertThat(categories).hasSize(2)
            .extracting("categoryName")
            .contains(EYE, BONE);
    }

    @DisplayName("카테고리 네임 목록을 통해 카테고리 객체를 조회할 수 있다.")
    @Test
    void 카테고리_조회2() throws Exception {
        //given//when
        Category category = categoryQueryRepository.findByCategoryName(EYE).orElse(Category.of(BONE));

        //then
        assertThat(category.getCategoryName()).isEqualByComparingTo(EYE);
    }
}