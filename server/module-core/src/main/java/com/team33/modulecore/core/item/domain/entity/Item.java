package com.team33.modulecore.core.item.domain.entity;

import com.team33.modulecore.core.category.domain.Categories;
import com.team33.modulecore.core.category.domain.CategoryName;
import com.team33.modulecore.core.category.infra.CategoryNameConverter;
import com.team33.modulecore.core.common.BaseEntity;
import com.team33.modulecore.core.item.domain.Information;
import com.team33.modulecore.core.item.domain.Statistic;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(
	name = "item",
	indexes = {
		@Index(name = "idx_item_discount_rate_price", columnList = "discount_rate, real_price"),
		@Index(name = "idx_enterprise", columnList = "enterprise")
	}
)
@Entity
public class Item extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "item_id")
	private Long id;

	@Embedded
	private Information information;

	@Embedded
	private Statistic statistics;

	@Enumerated(EnumType.STRING)
	@ElementCollection
	@CollectionTable(
		name = "item_category", joinColumns = @JoinColumn(name = "item_id"),
		indexes = @Index(name = "idx_item_category", columnList = "category_name")
	)
	@Column(name = "category_name")
	private Set<CategoryName> itemCategory = new HashSet<>();

	@ElementCollection
	@CollectionTable(name = "item_review", joinColumns = @JoinColumn(name = "item_id"))
	@Column(name = "review_id")
	private Set<Long> reviewIds = new HashSet<>();

	@Column(name = "categories")
	@Convert(converter = CategoryNameConverter.class)
	private Categories categories;

	@Builder
	private Item(
		Long id,
		Information information,
		Statistic statistics
	) {
		this.id = id;
		this.information = information;
		this.statistics = statistics;
	}

	public static Item create(Information information) {
		return Item.builder()
			.information(information)
			.statistics(new Statistic())
			.build();
	}

	public String getThumbnailUrl() {
		return this.information.getImage().getThumbnail();
	}

	public int getOriginPrice() {
		return this.information.getPrice().getOriginPrice();
	}

	public double getDiscountRate() {
		return this.information.getPrice().getDiscountRate();
	}

	public String getProductName() {
		return this.information.getProductName();
	}

	public int getDiscountPrice() {
		return this.information.getPrice().getDiscountPrice();
	}

	public int getRealPrice() {
		return this.information.getPrice().getRealPrice();
	}

	public String getDescriptionImage() {
		return this.getInformation().getImage().getDescriptionImage();
	}

	public int getSales() {
		return this.statistics.getSales();
	}

	public String getServingUse() {
		return this.information.getServingUse();
	}

	public double getStarAvg() {
		return this.statistics.getStarAvg();
	}

	public void addIncludedCategories(Set<CategoryName> categoryNames) {
		this.categories = new Categories(categoryNames);
	}

	public void addReviewId(Long id) {
		this.reviewIds.add(id);
	}

	public void addCountAndStars(double star) {
		this.statistics.addStarAvg(star);
	}

	public void deleteReviewId(Long reviewId) {
		this.reviewIds.remove(reviewId);
	}

	public void subtractCountAndStars(double star) {
		this.statistics.subtractStarAvg(star);
	}

	public void chageInformation(Information information) {
		this.information = information.toBuilder().build();
	}
}
