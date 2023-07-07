package com.team33.modulecore.domain.talk.repository;


import com.team33.modulecore.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.team33.modulecore.domain.talk.entity.TalkComment;

public interface TalkCommentRepository extends JpaRepository<TalkComment, Long> {

    List<TalkComment> findAllByUser(User user);
}
