package com.team33.moduleadmin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

// import com.team33.moduleadmin.dto.ItemInfoDto;
import com.team33.moduleadmin.dto.ItemDto;
import com.team33.modulecore.core.item.domain.Image;
import com.team33.modulecore.core.item.domain.Information;
import com.team33.modulecore.core.item.domain.Price;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class ItemAttributeMapper {


    public static List<Information> toInformation(List<ItemDto> items) {

        return items.stream()
            .map(
                wrapper ->Information.builder()
                    .baseStandard(wrapper.getBaseStandard())
                    .distributionPeriod(wrapper.getDistbPd())
                    .enterprise(wrapper.getEntrps())
                    .intake(wrapper.getIntakeHint1())
                    .mainFunction(wrapper.getMainFnctn())
                    .preservePeriod(wrapper.getPrsrvPd())
                    .registeredDate(wrapper.getPrsrvPd())
                    .productName(wrapper.getPrduct())
                    .servingUse(wrapper.getSrvUse())
                    .statementNumber(wrapper.getSttemntNo())
                    .sungsang(wrapper.getSungsang())
                    .price(new Price(wrapper.getOriginPrice(), wrapper.getDiscountRate()))
                    .image(new Image(wrapper.getImage(),wrapper.getDescription()))
                    .build()

            )
            .collect(Collectors.toUnmodifiableList());

    }

}
