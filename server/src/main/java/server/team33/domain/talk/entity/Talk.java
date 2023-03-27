package server.team33.domain.talk.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import server.team33.domain.audit.BaseEntity;
import server.team33.domain.user.entity.User;
import server.team33.domain.item.entity.Item;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Talk extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TALK_ID")
    private Long talkId;

    @ManyToOne
    @JoinColumn(name = "ITEM_ID")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private boolean shopper; // true == 해당 아이템을 구매한 유저

    @JsonIgnore
    @OneToMany(mappedBy = "talk", cascade = CascadeType.ALL)
    List<TalkComment> talkComments;
}
