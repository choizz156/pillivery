package com.team33.modulecore.item.application;

import com.team33.modulecore.item.domain.NutritionFact;
import com.team33.modulecore.item.dto.ItemPostServiceDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NutritionFactService {

    public List<NutritionFact> getNutritionFacts(ItemPostServiceDto dto){
        return NutritionFact.of(dto.getNutritionFacts());
    }
}
