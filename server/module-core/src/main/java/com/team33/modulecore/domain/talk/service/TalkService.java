package com.team33.modulecore.domain.talk.service;


import com.team33.modulecore.domain.talk.repository.TalkRepository;
import com.team33.modulecore.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.team33.modulecore.domain.item.entity.Item;
import com.team33.modulecore.domain.talk.entity.Talk;
import com.team33.modulecore.global.exception.BusinessLogicException;
import com.team33.modulecore.global.exception.ExceptionCode;

@Service
@Transactional
@RequiredArgsConstructor
public class TalkService {

    private final TalkRepository talkRepository;

    public Talk createTalk(Talk talk) { // 토크 작성 - 상세페이지
        return talkRepository.save(talk);
    }

    public Talk updateTalk(Talk talk) { // 토크 수정 - 상세페이지, 마이페이지
        Talk findTalk = findVerifiedTalk(talk.getTalkId());
        findTalk.setContent(talk.getContent());
        Talk updatedTalk = talkRepository.save(findTalk);

        return updatedTalk;
    }

    public Talk findTalk(long talkId) { // 특정 토크 찾기
        Talk talk = findVerifiedTalk(talkId);

        return talk;
    }

    public List<Talk> findTalks(User user) { // 유저의 토크 목록 조회
        List<Talk> talksByUser = talkRepository.findAllByUser(user);

        return talksByUser;
    }

    public Page<Talk> findItemTalks(Item item, int page, int size) {
        Page<Talk> pageTalk = talkRepository.findAllByItem(
                PageRequest.of(page, size, Sort.by("talkId").descending()), item);

        return pageTalk;
    }

    public void deleteTalk(long talkId, long userId) { // 토크 삭제 - 작성자만 가능
        Talk talk = findVerifiedTalk(talkId);
        long writerId = findTalkWriter(talkId);

        if(userId != writerId) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED_USER);
        }

        talkRepository.delete(talk);
    }

    public long findTalkWriter(long talkId) { // 작성자만 수정, 삭제를 할 수 있도록 작성자 찾기
        Talk talk = findVerifiedTalk(talkId);

        return talk.getUser().getUserId();
    }

    public Talk findVerifiedTalk(long talkId) {
        Optional<Talk> optionalTalk = talkRepository.findById(talkId);
        Talk findTalk = optionalTalk.orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.TALK_NOT_FOUND));

        return findTalk;
    }
}
