package com.team33.moduleadmin.service;

import static com.team33.modulecore.category.domain.CategoryName.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team33.modulecore.item.domain.Information;
import com.team33.modulecore.item.domain.entity.Item;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.jackson.plugin.JacksonPlugin;
import com.navercorp.fixturemonkey.javax.validation.plugin.JavaxValidationPlugin;

class ItemRegisterServiceTest {

	private FixtureMonkey FIXTURE_MONKEY = FixtureMonkey.builder()
		.plugin(new JavaxValidationPlugin())
		.plugin(new JacksonPlugin())
		.defaultNotNull(true)
		.build();

	@DisplayName("아이탬 객체를 생성할 수 있다.")
	@Test
	void 아이템_생성() throws Exception{
		//given
		List<Information> information = FIXTURE_MONKEY.giveMeBuilder(Information.class)
			.set("mainFunction", "눈")
			.sampleList(3);
		List<Information> information1 = FIXTURE_MONKEY.giveMeBuilder(Information.class)
			.set("mainFunction", "두뇌")
			.sampleList(3);
		information.addAll(information1);

		FakeItemCommandRepository itemCommandRepository = new FakeItemCommandRepository();
		ItemRegisterService itemRegisterService = new ItemRegisterService(itemCommandRepository);

		//when
		itemRegisterService.createItem(information);

		//then
		Item item1 = itemCommandRepository.findById(1L).orElse(null);
		Item item2 = itemCommandRepository.findById(3L).orElse(null);

		assertThat(item1.getCategories().getCategoryNameSet()).contains(EYE);
		assertThat(item1.getIncludedCategories()).contains(EYE);

		assertThat(item2.getCategories().getCategoryNameSet()).contains(BRAIN);
		assertThat(item2.getIncludedCategories()).contains(BRAIN);
	}

}