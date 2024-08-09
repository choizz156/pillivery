package com.team33.moduleadmin.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.team33.moduleadmin.dto.BodyWrapper;
import com.team33.moduleadmin.dto.ItemDto;
import com.team33.moduleadmin.dto.ItemWrapper;
import com.team33.moduleadmin.service.ItemAttributeMapper;
import com.team33.moduleadmin.service.ItemRegisterService;
import com.team33.modulecore.core.item.domain.Information;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.jackson.plugin.JacksonPlugin;
import com.navercorp.fixturemonkey.javax.validation.plugin.JavaxValidationPlugin;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/items")
public class ItemRegisterController {

    private final ItemRegisterService itemRegisterService;
	FixtureMonkey build = FixtureMonkey.builder()
		.plugin(new JavaxValidationPlugin())
		.plugin(new JacksonPlugin())
		.defaultNotNull(true)
		.build();

	private List<BodyWrapper> lists = new ArrayList<>(10000);

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
	public void postItem() throws URISyntaxException {

		RestTemplate restTemplate = new RestTemplate();
		for (int i = 71; i <= 300; i++) {
			String url = "https://apis.data.go.kr/1471000/HtfsInfoService03/getHtfsItem01?pageNo="
				+ i
				+ "&numOfRows=100&ServiceKey=yZVfQKw1vKRzc5MrFh8Y9y%2BdYV%2Fm465AOUrvcucC428QwNfRrKoJW%2Fx%2FsHz8EPBilmpaGn%2B%2BSM%2BYapFPeKshUg%3D%3D&type=json";

			URI uri = new URI(url);

			BodyWrapper forObject = restTemplate.getForObject(uri, BodyWrapper.class);
			lists.add(forObject);
		}

		log.warn("list size = {}", lists.size());

    }

	// @ResponseStatus(HttpStatus.CREATED)
	// @PostMapping("/test")
	// public void postItem1() {
	// 	List<ItemWrapper> itemWrappers = lists.stream()
	// 		.map(b -> b.getBody().getItems())
	// 		.flatMap(List::stream)
	// 		.collect(Collectors.toList());
	//
	// 	List<ItemDto> collect = toBusinessDto(itemWrappers);
	// 	List<Information> information = ItemAttributeMapper.toInformation(collect);
	// 	itemRegisterService.createItem(information);
	// }

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/test")
	public void postItem1(@RequestBody BodyWrapper bodyWrapper) {
		lists.add(bodyWrapper);

		List<ItemWrapper> itemWrappers = lists.stream()
			.map(b -> b.getBody().getItems())
			.flatMap(List::stream)
			.collect(Collectors.toList());

		List<ItemDto> collect = toBusinessDto(itemWrappers);
		List<Information> information = ItemAttributeMapper.toInformation(collect);
		itemRegisterService.createItem(information);
	}



    private List<ItemDto> toBusinessDto(List<ItemWrapper> items) {


        return items.stream().map(wrapper ->
            build.giveMeBuilder(ItemDto.class)
                .set("BASE_STANDARD", wrapper.getItem().getBaseStandard())
                .set("DISTB_PD", wrapper.getItem().getDistbPd())
                .set("ENTRPS", wrapper.getItem().getEntrps())
                .set("INTAKE_HINT1", wrapper.getItem().getIntakeHint1())
                .set("MAIN_FNCTN", wrapper.getItem().getMainFnctn())
                .set("PRDUCT", wrapper.getItem().getPrduct())
                .set("PRSRV_PD", wrapper.getItem().getPrsrvPd())
                .set("SRV_USE", wrapper.getItem().getSrvUse())
                .set("STTEMNT_NO", wrapper.getItem().getSttemntNo())
                .set("REGIST_DT", wrapper.getItem().getRegistDt())
                .set("SUNGSANG", wrapper.getItem().getSungsang())
                .sample()

        ).collect(Collectors.toList());

    }

}
