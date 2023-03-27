package server.team33.domain.talk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.team33.domain.user.entity.User;
import server.team33.domain.talk.entity.TalkComment;

import java.util.List;

public interface TalkCommentRepository extends JpaRepository<TalkComment, Long> {

    List<TalkComment> findAllByUser(User user);
}
