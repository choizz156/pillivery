package team33.modulecore.domain.talk.repository;


import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import team33.modulecore.domain.item.entity.Item;
import team33.modulecore.domain.talk.entity.Talk;
import team33.modulecore.domain.user.entity.User;

public interface TalkRepository extends JpaRepository<Talk, Long> {

    List<Talk> findAllByUser(User user);

    Page<Talk> findAllByItem(Pageable pageable, Item item);
}
