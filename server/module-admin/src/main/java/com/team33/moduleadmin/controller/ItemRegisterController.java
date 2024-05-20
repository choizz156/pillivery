package com.team33.moduleadmin.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
import com.team33.modulecore.item.domain.Information;

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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
	public void postItem() {
		List<BodyWrapper> lists = new ArrayList<>(10000);
		RestTemplate restTemplate = new RestTemplate();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_HTML));
		restTemplate.getMessageConverters().add(converter);

		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		final HttpEntity<?> entity = new HttpEntity<>(headers);
		for (int i = 10; i <= 20 ; i++) {
			String url = "https://apis.data.go.kr/1471000/HtfsInfoService03/getHtfsItem01?pageNo="
				+ i
				+ "&numOfRows=100&ServiceKey=yZVfQKw1vKRzc5MrFh8Y9y%2BdYV%2Fm465AOUrvcucC428QwNfRrKoJW%2Fx%2FsHz8EPBilmpaGn%2B%2BSM%2BYapFPeKshUg%3D%3D%3D%3D&type=json";

			BodyWrapper forObject = restTemplate.getForObject(url, BodyWrapper.class);
			lists.add(forObject);
		}


		List<ItemWrapper> itemWrappers = lists.stream()
			.map(b -> b.getBody().getItems())
			.flatMap(List::stream)
			.collect(Collectors.toList());

		List<ItemDto> collect = toBusinessDto(itemWrappers);
        List<Information> information = ItemAttributeMapper.toInformation(collect);
        itemRegisterService.createItem(information);
    }

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/test")
	public void postItem1(@RequestBody BodyWrapper bodyWrapper) {
		List<BodyWrapper> lists = new ArrayList<>(10000);
		// RestTemplate restTemplate = new RestTemplate();

		// for(int i = 1; i <= 1; i++) {
		// 	BodyWrapper forObject = restTemplate.getForObject(
		// 		"https://apis.data.go.kr/1471000/HtfsInfoService03/getHtfsItem01?pageNo="
		// 			+ i + "&numOfRows=100&ServiceKey=yZVfQKw1vKRzc5MrFh8Y9y%2BdYV%2Fm465AOUrvcucC428QwNfRrKoJW%2Fx%2FsHz8EPBilmpaGn%2B%2BSM%2BYapFPeKshUg%3D%3D&type=json",
		// 		BodyWrapper.class);

		// }
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
		items.forEach(itemInfoDto -> log.warn("{}", itemInfoDto.getItem().getEntrps()));
        FixtureMonkey build = FixtureMonkey.builder()
            .plugin(new JavaxValidationPlugin())
            .plugin(new JacksonPlugin())
            .defaultNotNull(true)
            .build();

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
