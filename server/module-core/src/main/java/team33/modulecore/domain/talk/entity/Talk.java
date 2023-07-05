package team33.modulecore.domain.talk.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team33.modulecore.domain.audit.BaseEntity;
import team33.modulecore.domain.item.entity.Item;
import team33.modulecore.domain.user.entity.User;


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
