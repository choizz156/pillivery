package com.team33.modulecore.talk.repository;


import com.team33.modulecore.user.domain.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.team33.modulecore.item.domain.Item;
import com.team33.modulecore.talk.domain.Talk;

public interface TalkRepository extends JpaRepository<Talk, Long> {

    List<Talk> findAllByUser(User user);

    Page<Talk> findAllByItem(Pageable pageable, Item item);
}
