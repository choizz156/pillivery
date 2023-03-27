package server.team33.domain.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.team33.domain.item.entity.Brand;

@Service
@RequiredArgsConstructor
public class BrandService {

    public void verifyExistBrand(Brand brand) {
        Brand.valueOf(String.valueOf(brand)); // Enum에 존재하지 않은 속성값일 경우 에러
    }
}
