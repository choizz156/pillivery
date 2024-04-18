package com.team33.modulecore.talk.repository;


import com.team33.modulecore.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.team33.modulecore.talk.domain.TalkComment;

public interface TalkCommentRepository extends JpaRepository<TalkComment, Long> {

    List<TalkComment> findAllByUser(User user);
}
