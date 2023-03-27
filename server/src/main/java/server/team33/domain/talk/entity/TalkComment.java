package server.team33.domain.talk.entity;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import server.team33.domain.audit.BaseEntity;
import server.team33.domain.user.entity.User;
import server.team33.domain.item.entity.Item;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class TalkComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long talkCommentId;

    @ManyToOne
    @JoinColumn(name = "ITEM_ID")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "TALK_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Talk talk;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private boolean shopper;
}
