package server.team33.domain.talk.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import server.team33.domain.user.entity.User;
import server.team33.domain.item.entity.Item;
import server.team33.domain.talk.entity.Talk;

import java.util.List;

public interface TalkRepository extends JpaRepository<Talk, Long> {

    List<Talk> findAllByUser(User user);

    Page<Talk> findAllByItem(Pageable pageable, Item item);
}
