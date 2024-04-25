package com.team33.modulecore.item.application;

import com.team33.modulecore.item.domain.NutritionFact;
import com.team33.modulecore.item.dto.ItemPostServiceDto;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//transcational 있어야됨????
@Service
@RequiredArgsConstructor
public class NutritionFactsService {

    public Set<NutritionFact> getNutritionFacts(ItemPostServiceDto dto){
        return NutritionFact.of(dto.getNutritionFacts());
    }
}
