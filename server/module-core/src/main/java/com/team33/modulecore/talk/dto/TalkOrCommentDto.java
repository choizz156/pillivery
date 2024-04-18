package com.team33.modulecore.talk.dto;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.team33.modulecore.item.dto.ItemSimpleResponseDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TalkOrCommentDto { // 마이페이지 - 토크

    private long talkId;
    private long talkCommentId;
    private ItemSimpleResponseDto item;
    private String content;
    private boolean reply; // true 일경우 토크 코멘트 , false 이면 토크
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
