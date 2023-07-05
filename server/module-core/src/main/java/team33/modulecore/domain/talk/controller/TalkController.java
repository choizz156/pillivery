package team33.modulecore.domain.talk.controller;

import java.util.List;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RestController;
import team33.modulecore.domain.item.mapper.ItemMapper;
import team33.modulecore.domain.item.service.ItemService;
import team33.modulecore.domain.order.service.OrderService;
import team33.modulecore.domain.talk.dto.TalkDto;
import team33.modulecore.domain.talk.dto.TalkOrCommentDto;
import team33.modulecore.domain.talk.entity.Talk;
import team33.modulecore.domain.talk.entity.TalkComment;
import team33.modulecore.domain.talk.mapper.TalkMapper;
import team33.modulecore.domain.talk.service.TalkCommentService;
import team33.modulecore.domain.talk.service.TalkService;
import team33.modulecore.domain.user.entity.User;
import team33.modulecore.domain.user.service.UserService;
import team33.modulecore.global.response.MultiResponseDto;
import team33.modulecore.global.response.SingleResponseDto;


@Slf4j
@Validated
@CrossOrigin
@RestController
@RequestMapping("/talks")
@RequiredArgsConstructor
public class TalkController {
    private final TalkService talkService;
    private final TalkMapper talkMapper;
    private final TalkCommentService commentService;

    private final UserService userService;
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final OrderService orderService;

    @PostMapping("/{item-id}") // 주문 상세 내역에서 작성
    public ResponseEntity postTalk(@PathVariable("item-id") @Positive long itemId,
                                   @RequestBody TalkDto talkDto) {

        Talk talk = talkService.createTalk(
                talkMapper.talkDtoToTalk(itemId, userService, orderService, itemService, talkDto));

        return  new ResponseEntity<>(
                new SingleResponseDto<>(talkMapper.talkToTalkResponseDto(talk)), HttpStatus.CREATED);
    }

    @PatchMapping("/{talk-id}")
    public ResponseEntity updateTalk(@PathVariable("talk-id") @Positive long talkId,
                                     @RequestBody TalkDto talkDto) {

        Talk talk = talkMapper.talkDtoToTalk(talkId, userService, talkService, talkDto);

        Talk updatedTalk = talkService.updateTalk(talk);

        return new ResponseEntity<>(new SingleResponseDto<>(
                talkMapper.talkToTalkResponseDto(updatedTalk)), HttpStatus.OK);
    }

    @GetMapping("/{talk-id}")
    public ResponseEntity getTalk(@PathVariable("talk-id") @Positive long talkId) {

        Talk talk = talkService.findTalk(talkId);

        return new ResponseEntity<>(new SingleResponseDto<>(
                talkMapper.talkToTalkResponseDto(talk)), HttpStatus.OK);
    }

    @GetMapping("/mypage") // 유저가 작성한 토크, 토크 코멘트 한번에 ! 토크일 경우 토크에 달린 코멘트는 X
    public ResponseEntity getUserTalk(Pageable pageable) {

        User user = userService.getLoginUser();
        List<Talk> talks = talkService.findTalks(user);
        List<TalkComment> talkComments = commentService.findTalkComments(user);

        List<TalkOrCommentDto> talkOrCommentDtos = talkMapper.toTalkOrCommentDtos(talks, talkComments, itemMapper);
        Page<TalkOrCommentDto> talkOrCommentDtoPage = talkMapper.toPageDtos(pageable, talkOrCommentDtos);

        return new ResponseEntity<>(new MultiResponseDto<>(talkOrCommentDtos, talkOrCommentDtoPage), HttpStatus.OK);
    }

    @DeleteMapping("/{talk-id}")
    public ResponseEntity deleteTalk(@PathVariable("talk-id") @Positive long talkId) {

        User user = userService.getLoginUser();
        talkService.deleteTalk(talkId, user.getUserId());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
