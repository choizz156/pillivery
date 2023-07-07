package com.team33.modulecore.domain.review.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.team33.modulecore.domain.audit.BaseEntity;
import com.team33.modulecore.domain.item.entity.Item;
import com.team33.modulecore.domain.user.entity.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "ITEM_ID")
    @OnDelete(action = OnDeleteAction.CASCADE) // 아이템 삭제시 해당 itemId 를 참조하는 리뷰 삭제
    private Item item;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    private int quantity; // 리뷰를 작성하는 주문의 아이템 구매 수량

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private int star;
}
