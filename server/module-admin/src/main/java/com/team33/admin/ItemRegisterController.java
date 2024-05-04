package com.team33.admin;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.jackson.plugin.JacksonPlugin;
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin;
import com.team33.admin.dto.BodyWrapper;
import com.team33.admin.dto.ItemDto;
import com.team33.admin.dto.ItemWrapper;
import com.team33.modulecore.item.domain.Information;
import com.team33.modulecore.item.domain.repository.ItemCommandRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/items", method = RequestMethod.POST)
public class ItemRegisterController {

    private final ItemCommandRepository itemCommandRepository;
    private final com.team33.moduleapi.admin.ItemRegisterService itemRegisterService;

    @ResponseStatus(HttpStatus.CREATED)
    public void postItem(@RequestBody BodyWrapper bodyWrapper) {
        List<ItemDto> collect = toBusinessDto(bodyWrapper.getBody().getItems());
        List<Information> information = ItemAttributeMapper.toInformation(collect);
        itemRegisterService.createItem(information);
    }

    private List<ItemDto> toBusinessDto(List<ItemWrapper> items) {
        FixtureMonkey build = FixtureMonkey.builder()
            .plugin(new JakartaValidationPlugin())
            .plugin(new JacksonPlugin())
            .defaultNotNull(true)
            .build();

        return items.stream().map(wrapper -> {
            return build.giveMeBuilder(ItemDto.class)
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
                .sample();

        }).collect(Collectors.toList());

    }

}
