package server.team33.user.entity;

import static server.team33.user.entity.UserRoles.USER;
import static server.team33.user.entity.UserStatus.USER_ACTIVE;

import lombok.*;
import server.team33.audit.Auditable;
import server.team33.cart.entity.Cart;
import server.team33.order.entity.Order;
import server.team33.user.dto.UserPostDto;
import server.team33.wish.entity.Wish;

import javax.persistence.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "USERS")
public class User extends Auditable implements Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(updatable = false)
    private String email;

    @Column(name = "DISPALY_NAME", length = 20)
    private String displayName;
    private String password;
    private String address;
    @Column(name = "Datail_ADDRESS")
    private String detailAddress;
    @Column(name = "REAL_NAME")
    private String realName;
    @Column(unique = true)
    private String phone;
    @Column(name = "OAUTH_ID")
    private String oauthId;
    private String provider;
    @Column(name = "PROVIDER_ID")
    private String providerId;
    private String sid;

    @Enumerated(EnumType.STRING)
    private UserRoles roles;

    @Enumerated(value = EnumType.STRING)
    private UserStatus userStatus;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Wish> wishList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    @Builder
    private User(   String email,
                    String displayName,
                    String password,
                    String address,
                    String detailAddress,
                    String realName,
                    String phone,
                    UserRoles roles,
                    UserStatus userStatus
    ) {
        this.email = email;
        this.displayName = displayName;
        this.password = password;
        this.address = address;
        this.detailAddress = detailAddress;
        this.realName = realName;
        this.phone = phone;
        this.roles = roles;
        this.userStatus = userStatus;
    }

    public static User createUser(UserPostDto userDto) {
        return User.builder().email(userDto.getEmail())
            .displayName(userDto.getDisplayName())
            .password(userDto.getPassword())
            .address(userDto.getAddress())
            .detailAddress(userDto.getDetailAddress())
            .realName(userDto.getRealName())
            .phone(userDto.getPhone())
            .roles(USER)
            .userStatus(USER_ACTIVE)
            .build();
    }

    @Override
    public String getName(){
        return getEmail();
    }


    public void applyEncyrptPassword(String encryptedPwd) {
        this.password = encryptedPwd;
    }

    public void addCart(Cart cart) {
        this.cart = cart;
    }
}


