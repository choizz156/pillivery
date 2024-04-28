package com.team33.modulecore;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class RepositorySetting {

    private static EntityManagerFactory emf;
    private static EntityManager em;

    private static final FixtureMonkey FIXTURE_MONKEY = FixtureMonkey
        .builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .defaultNotNull(true)
        .build();


    @BeforeAll
    static void beforeAll() {
        emf = Persistence.createEntityManagerFactory("test");
        em = emf.createEntityManager();
        em.getTransaction().begin();
    }

    @AfterAll
    static void afterAll() {
        em.getTransaction().rollback(); // 커넥션 반납용 롤백
        em.close();
        emf.close();
    }

    public static EntityManager getEmAtSuperClass() {
        return em;
    }
}
