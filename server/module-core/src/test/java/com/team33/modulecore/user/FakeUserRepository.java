package com.team33.modulecore.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.team33.modulecore.FixtureMonkeyFactory;
import com.team33.modulecore.order.domain.Address;
import com.team33.modulecore.user.domain.User;
import com.team33.modulecore.user.domain.repository.UserRepository;

public class FakeUserRepository implements UserRepository {

	private Map<Long, User> users;

	public FakeUserRepository() {
		this.users = new HashMap<>();

		User user = FixtureMonkeyFactory.get().giveMeBuilder(User.class)
			.set("id", 1L)
			.set("email", "test1@gmail.com")
			.set("displayName", "test22")
			.set("phone", "010-1112-1111")
			.set("password", "password")
			.set("address", new Address("서울시 부평구 송도동", "101 번지"))
			.set("realName", "홍길동")
			.set("cartId", 1L)
			.sample();

		users.put(user.getId(), user);
	}

	@Override
	public User save(User user) {
		users.put(4L, user);
		return user;
	}

	@Override
	public Optional<User> findById(Long id) {
		return Optional.ofNullable(users.get(id));
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return users.values().stream()
			.filter(user -> user.getEmail().equals(email))
			.findFirst();
	}

	@Override
	public Optional<User> findByDisplayName(String displayName) {
		return users.values().stream()
			.filter(user -> user.getDisplayName().equals(displayName))
			.findFirst();
	}

	@Override
	public Optional<User> findByPhone(String phoneNumber) {
		return users.values().stream()
			.filter(user -> user.getPhone().equals(phoneNumber))
			.findFirst();
	}
}
