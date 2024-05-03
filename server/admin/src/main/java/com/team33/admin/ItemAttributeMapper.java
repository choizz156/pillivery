package com.team33.moduleapi.admin;

import com.team33.moduleapi.admin.dto.ItemDto;
import com.team33.modulecore.item.domain.ItemPrice;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
class ItemAttributeMapper {


    public static List<com.team33.modulecore.item.domain.ItemInformation> toInformation(List<ItemDto> items) {
        return items.stream()
            .map(
                wrapper -> com.team33.modulecore.item.domain.ItemInformation.builder()
                    .baseStandard(wrapper.getBaseStandard())
                    .distributionPeriod(wrapper.getDistbPd())
                    .enterprise(wrapper.getEntrps())
                    .intake(wrapper.getIntakeHint1())
                    .mainFunction(wrapper.getMainFnctn())
                    .preservePeriod(wrapper.getPrsrvPd())
                    .registeredDate(wrapper.getPrsrvPd())
                    .product(wrapper.getPrduct())
                    .servingUse(wrapper.getSrvUse())
                    .statementNumber(wrapper.getSttemntNo())
                    .sungsang(wrapper.getSungsang())
                    .itemPrice(new ItemPrice(wrapper.getOriginPrice(), wrapper.getDiscountRate()))
                    .build()

            )
            .collect(Collectors.toUnmodifiableList());

    }

}
