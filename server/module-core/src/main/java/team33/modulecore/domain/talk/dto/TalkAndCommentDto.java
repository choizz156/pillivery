package team33.modulecore.domain.talk.dto;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TalkAndCommentDto { // 상세페이지 - 토크

    private long talkId;
    private long userId;
    private String displayName;
    private long itemId;
    private String content;
    private boolean shopper;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private List<TalkCommentDto> talkComments;
}
