package com.team33.modulecore.core.item.domain.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.core.item.domain.entity.Item;

@ActiveProfiles("test")
@EnableAutoConfiguration
@EnableJpaRepositories("com.team33.modulecore.core.item.domain.repository")
@EntityScan("com.team33.modulecore.core.item.domain.entity")
@ContextConfiguration(classes = {ItemViewBatchDao.class, Item.class, ItemCommandRepository.class})
@SpringBootTest
class ItemViewBatchDaoTest {

	@Autowired
	private ItemCommandRepository itemCommandRepository;

	@Autowired
	private ItemViewBatchDao itemViewBatchDao;

	@DisplayName("조회수를 batch로 증가시킬 수 있다.")
	@Test
	void 조회수_증가_배치() {

		List<Item> items = FixtureMonkeyFactory.get().giveMeBuilder(Item.class)
			.setNull("id")
			.setNull("categories")
			.setNull("reviewIds")
			.set("information.price.discountPrice",0)
			.set("statistics.view", 0)
			.sampleList(10);

		itemCommandRepository.saveAll(items);

		Map<String, Long> map =new HashMap<>(11);

		for (int i = 1; i < 11; i++) {
			map.put(String.valueOf(i), 1L);
		}

		itemViewBatchDao.updateAll(map);

		long view = itemCommandRepository.findById(2L).get().getStatistics().getView();
		assertThat(view).isEqualTo(1L);
	}

}