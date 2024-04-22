package com.team33.modulecore.wish.domain;

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
import com.team33.modulecore.item.domain.Item;
import com.team33.modulecore.user.domain.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishId;


    @ManyToOne
    @JoinColumn(name = "ITEM_ID")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column
    private int isWish;


    public void addItem(Item item) {
        if (this.item == null && item != null) {
            this.item = item;
        }
    }

    public void addUser(User user) {
        if (this.user == null && user != null) {
            this.user = user;
        }
    }
}