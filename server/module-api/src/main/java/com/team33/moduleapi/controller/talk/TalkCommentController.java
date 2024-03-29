package com.team33.moduleapi.controller.talk;

import com.team33.modulecore.domain.item.service.ItemService;
import com.team33.modulecore.domain.order.service.OrderService;
import com.team33.modulecore.domain.talk.dto.TalkDto;
import com.team33.modulecore.domain.talk.entity.TalkComment;
import com.team33.modulecore.domain.talk.mapper.TalkMapper;
import com.team33.modulecore.domain.talk.service.TalkCommentService;
import com.team33.modulecore.domain.talk.service.TalkService;
import com.team33.modulecore.domain.user.entity.User;
import com.team33.modulecore.domain.user.service.UserService;
import com.team33.moduleapi.response.SingleResponseDto;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@Validated
@CrossOrigin
@RestController
@RequestMapping("/talks/comments")
@RequiredArgsConstructor
public class TalkCommentController {

    private final TalkCommentService commentService;
    private final TalkMapper talkMapper;
    private final TalkService talkService;
    private final UserService userService;
    private final OrderService orderService;
    private final ItemService itemService;

    @PostMapping("/{talk-id}") // 토크 코멘트 등록
    public ResponseEntity postTalkComment(@PathVariable("talk-id") @Positive long talkId,
                                          @RequestParam(value="itemId") long itemId,
                                          @RequestBody TalkDto talkDto) {

        TalkComment talkComment = commentService.createTalkComment(talkMapper.talkDtoToTalkComment(
                itemId, talkId, userService, orderService, talkService, itemService, talkDto));

        return new ResponseEntity<>(new SingleResponseDto<>(
                talkMapper.talkCommentToTalkCommentDto(talkComment)), HttpStatus.CREATED);
    }

    @PatchMapping("/{talkComment-id}") // 토크 코멘트 수정 - 상세페이지, 작성글 목록
    public ResponseEntity updateTalkComment(@PathVariable("talkComment-id") @Positive long talkCommentId,
                                            @RequestBody TalkDto talkDto) {

        TalkComment talkComment = talkMapper.talkDtoToTalkComment(talkCommentId, userService, commentService, talkDto);

        TalkComment updatedComment = commentService.updateTalkComment(talkComment);

        return new ResponseEntity<>(new SingleResponseDto<>(
                talkMapper.talkCommentToTalkCommentDto(updatedComment)), HttpStatus.OK);
    }

    @GetMapping("/{talkComment-id}")
    public ResponseEntity getTalkComment(@PathVariable("talkComment-id") @Positive long talkCommentId) {

        TalkComment talkComment = commentService.findTalkComment(talkCommentId);

        return new ResponseEntity<>(new SingleResponseDto<>(
                talkMapper.talkCommentToTalkCommentDto(talkComment)), HttpStatus.OK);
    }

    @DeleteMapping("/{talkComment-id}") // 토크 코멘트 삭제 - 상세페이지, 작성글 목록
    public ResponseEntity deleteTalk(@PathVariable("talkComment-id") @Positive long talkCommentId) {

        User user = userService.getLoginUser();
        commentService.deleteTalk(talkCommentId, user.getUserId());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
