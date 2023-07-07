package com.team33.modulecore.domain.user.entity;

import com.team33.modulecore.domain.order.entity.Order;
import com.team33.modulecore.domain.user.dto.UserPatchDto;
import com.team33.modulecore.domain.user.dto.UserPostDto;
import com.team33.modulecore.domain.wish.entity.Wish;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.team33.modulecore.domain.audit.BaseEntity;
import com.team33.modulecore.domain.cart.entity.Cart;
import com.team33.modulecore.domain.user.dto.UserPostOauthDto;


@Slf4j
@Getter
@NoArgsConstructor
@Entity
@Table(name = "USERS")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(updatable = false)
    private String email;
    @Column(length = 20)
    private String displayName;
    private String password;
    @Embedded
    private Address address;
    @Column(name = "REAL_NAME")
    private String realName;
    @Column(unique = true)
    private String phone;
    @Column(name = "OAUTH_ID")
    private String oauthId;
    @Enumerated(EnumType.STRING)
    private UserRoles roles;
    @Enumerated(value = EnumType.STRING)
    private UserStatus userStatus;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Wish> wishList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @Builder
    private User(final Long userId, String email,
        String displayName,
        String password,
        Address address,
        String realName,
        String phone,
        UserRoles roles,
        UserStatus userStatus,
        String oauthId
    ) {
        this.userId = userId;
        this.email = email;
        this.displayName = displayName;
        this.password = password;
        this.address = address;
        this.realName = realName;
        this.phone = phone;
        this.roles = roles;
        this.userStatus = userStatus;
        this.oauthId = oauthId;
    }

    public static User createUser(UserPostDto userDto) {
        Address address = new Address(userDto.getCity(), userDto.getDetailAddress());
        return User.builder().email(userDto.getEmail())
            .displayName(userDto.getDisplayName())
            .password(userDto.getPassword())
            .address(address)
            .realName(userDto.getRealName())
            .phone(userDto.getPhone())
            .roles(UserRoles.USER)
            .userStatus(UserStatus.USER_ACTIVE)
            .build();
    }

    public void applyEncryptPassword(String encryptedPwd) {
        this.password = encryptedPwd;
    }

    public void addCart(Cart cart) {
        this.cart = cart;
    }

    public void addAdditionalOauthUserInfo(UserPostOauthDto userDto) {
        this.address = new Address(userDto.getCity(), userDto.getDetailAddress());
        this.displayName = userDto.getDisplayName();
        this.phone = userDto.getPhone();
    }

    public void updateUserInfo(UserPatchDto userDto) {
        this.address = new Address(userDto.getCity(), userDto.getDetailAddress());
        this.displayName = userDto.getDisplayName();
        this.phone = userDto.getPhone();
        this.realName = userDto.getRealName();
    }

    public void withdrawal() {
        this.userStatus = UserStatus.USER_WITHDRAWAL;
    }
}


