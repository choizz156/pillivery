package com.team33.modulecore.item.domain.repository;


import com.team33.modulecore.item.domain.entity.Item;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemQueryRepository {


//    @Query("SELECT i FROM Item i join Category c on i.id = c.item.id WHERE c.categoryName  = :categoryName")
//    Page<Item> findAllByCategoryName(Pageable pageable, @Param("categoryName") String categoryName);
//
//
//    @Query("SELECT i FROM Item i join Category c on i.id = c.item.id WHERE c.categoryName = :categoryName AND i.brand = :brand")
//    Page<Item> findAllCategoryNameAndBrand(Pageable pageable,
//        @Param("categoryName") String categoryName, @Param("brand") Brand brand);
//
//
//    @Query("SELECT i FROM Item i join Category c on i.id = c.item.id WHERE c.categoryName = :categoryName AND i.discountRate > 0")
//    Page<Item> findAllCategoryNameAndDiscountRate(Pageable pageable,
//        @Param("categoryName") String categoryName);
//
//
//    @Query(value = "SELECT i FROM Item i join Category c on i.id = c.item.id WHERE c.categoryName = :categoryName AND i.brand = :brand AND i.discountRate > 0")
//    Page<Item> findAllCategoryNameAndDiscountRateAndBrand(Pageable pageable,
//        @Param("categoryName") String categoryName, @Param("brand") Brand brand);


    //TODO: 이거는 캐싱을 해놔야할 듯...
    List<Item> findTop9ByOrderBySalesDesc();

    List<Item> findTop9ByOrderByItemPriceDiscountRateDesc();

//    Page<Item> findByTitleContaining(Pageable pageable, String keyword);
//
//    Page<Item> findByPriceBetween(Pageable pageable, int low, int high);
//
//    Page<Item> findByTitleContainingAndPriceBetween(Pageable pageable, String keyword, int low,
//        int high);
//
//    Page<Item> findByTitleContainingAndDiscountRateGreaterThan(Pageable pageable, String keyword,
//        int minRate);
//
//    Page<Item> findByTitleContainingAndDiscountRateGreaterThanAndPriceBetween(Pageable pageable,
//        String keyword, int minRate, int low, int high);
//
//    @Query("SELECT i FROM Item i JOIN Category c on i.id = c.item.id where c.categoryName = :categoryName and i.price > :low and i.price < :high")
//    Page<Item> findByCategoryNameAndPriceBetween(Pageable pageable,
//        @Param("categoryName") String categoryName, @Param("low") int low, @Param("high") int high);
//
//
//    @Query("SELECT i FROM Item i JOIN Category c on i.id = c.item.id where c.categoryName = :categoryName and i.discountRate > 0 and i.price > :low and i.price < :high")
//    Page<Item> findByCategoryNameAndSaleAndPriceBetween(Pageable pageable,
//        @Param("categoryName") String categoryName, @Param("low") int low, @Param("high") int high);
//
//
//    @Query("SELECT i FROM Item i join Category c on i.id = c.item.id WHERE c.categoryName = :categoryName AND i.brand = :brand and i.price > :low and i.price < :high")
//    Page<Item> findByCategoryNameAndBrandAndPriceBetween(Pageable pageable,
//        @Param("categoryName") String categoryName, @Param("brand") Brand brand,
//        @Param("low") int low, @Param("high") int high);
//
//
//    @Query("SELECT i FROM Item i join Category c on i.id = c.item.id WHERE c.categoryName = :categoryName AND i.brand = :brand and i.discountRate > 0 and i.price > :low and i.price < :high")
//    Page<Item> findByCategoryNameAndBrandAndSaleAndPriceBetween(Pageable pageable,
//        @Param("categoryName") String categoryName, @Param("brand") Brand brand,
//        @Param("low") int low, @Param("high") int high);
}
