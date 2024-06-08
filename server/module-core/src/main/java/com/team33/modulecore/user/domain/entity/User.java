package com.team33.modulecore.user.domain.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
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

import com.team33.modulecore.common.BaseEntity;
import com.team33.modulecore.user.domain.Address;
import com.team33.modulecore.user.domain.UserRoles;
import com.team33.modulecore.user.domain.UserStatus;
import com.team33.modulecore.user.dto.OAuthUserServiceDto;
import com.team33.modulecore.user.dto.UserServicePatchDto;
import com.team33.modulecore.user.dto.UserServicePostDto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users", indexes = {
	@Index(name = "idx_email", columnList = "email"),
	@Index(name = "idx_phone", columnList = "phone"),
	@Index(name = "idx_display_name", columnList = "displayName")
})
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	private String email;

	private String displayName;

	private String password;

	private String realName;

	private String phone;

	@Embedded
	private Address address;

	private String oauthId;

	@Enumerated(EnumType.STRING)
	private UserRoles roles;

	@Enumerated(value = EnumType.STRING)
	private UserStatus userStatus;

	private Long normalCartId;

	private Long subscriptionCartId;

	@ElementCollection
	@CollectionTable(name = "user_review", joinColumns = @JoinColumn(name = "user_id"))
	@Column(name = "review_id")
	private Set<Long> reviewIds = new HashSet<>();

	@Builder
	private User(
		String email,
		String displayName,
		String password,
		Address address,
		String realName,
		String phone,
		UserRoles roles,
		UserStatus userStatus,
		String oauthId
	) {
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

		return User.builder()
			.email(userDto.getEmail())
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
		if (this.password.equals(encryptedPwd))
			return;
		this.password = encryptedPwd;
	}

	public void addCart(Long normalCartId, Long subscriptionCartId) {
		this.normalCartId = normalCartId;
		this.subscriptionCartId = subscriptionCartId;
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
		this.normalCartId = null;
		this.subscriptionCartId = null;
	}

	public String getCityAtAddress() {
		return this.address.getCity();
	}

	public String getDetailAddress() {
		return this.address.getDetailAddress();
	}

	public void addReviewId(Long id) {
		this.reviewIds.add(id);
	}

	public void deleteReviewId(Long reviewId) {
		this.reviewIds.remove(id);
	}
}



