package com.team33.modulecore.user.domain;

import com.team33.modulecore.cart.domain.Cart;
import com.team33.modulecore.common.BaseEntity;
import com.team33.modulecore.order.domain.Address;
import com.team33.modulecore.user.dto.OAuthUserServiceDto;
import com.team33.modulecore.user.dto.UserServicePatchDto;
import com.team33.modulecore.user.dto.UserServicePostDto;
import com.team33.modulecore.wish.domain.Wish;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false)
    private String email;

    @Column(length = 20)
    private String displayName;

    private String password;

    private String realName;

    @Column(unique = true)
    private String phone;

    @Embedded
    private Address address;

    private String oauthId;

    @Enumerated(EnumType.STRING)
    private UserRoles roles;

    @Enumerated(value = EnumType.STRING)
    private UserStatus userStatus;

    @OneToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wish> wishList = new ArrayList<>();

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Order> orders = new ArrayList<>();

    @Builder
    private User(final Long id, String email,
        String displayName,
        String password,
        Address address,
        String realName,
        String phone,
        UserRoles roles,
        UserStatus userStatus,
        String oauthId
    ) {
        this.id = id;
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

    public static User createUser(UserServicePostDto userDto, String encryptedPassword) {

        return User.builder().email(userDto.getEmail())
            .displayName(userDto.getDisplayName())
            .address(userDto.getAddress())
            .password(encryptedPassword)
            .realName(userDto.getRealName())
            .phone(userDto.getPhone())
            .roles(UserRoles.USER)
            .userStatus(UserStatus.USER_ACTIVE)
            .build();
    }

    public void applyEncryptPassword(String encryptedPwd) {
        if(this.password.equals(encryptedPwd)) return;
        this.password = encryptedPwd;
    }

    public void addCart(Cart cart) {
        this.cart = cart;
    }

    public void addAdditionalOauthUserInfo(OAuthUserServiceDto userDto) {
        this.address = userDto.getAddress();
        this.displayName = userDto.getDisplayName();
        this.phone = userDto.getPhone();
    }

    public void updateUserInfo(UserServicePatchDto userDto) {
        this.address = userDto.getAddress();
        this.displayName = userDto.getDisplayName();
        this.phone = userDto.getPhone();
        this.realName = userDto.getRealName();
    }

    public void withdrawal() {
        this.userStatus = UserStatus.USER_WITHDRAWAL;
    }

    public String getCityAtAddress(){
        return this.address.getCity();
    }

    public String getDetailAddress(){
        return this.address.getDetailAddress();
    }
}


