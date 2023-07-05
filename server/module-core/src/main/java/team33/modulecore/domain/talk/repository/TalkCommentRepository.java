package team33.modulecore.domain.talk.repository;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import team33.modulecore.domain.talk.entity.TalkComment;
import team33.modulecore.domain.user.entity.User;

public interface TalkCommentRepository extends JpaRepository<TalkComment, Long> {

    List<TalkComment> findAllByUser(User user);
}
