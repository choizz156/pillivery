package com.team33.admin;

import com.team33.admin.dto.ItemDto;
import com.team33.modulecore.item.domain.Information;
import com.team33.modulecore.item.domain.Price;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
class ItemAttributeMapper {


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
                    .build()

            )
            .collect(Collectors.toUnmodifiableList());

    }

}
