package study.data_jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"})
public class Team extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team") // FK 없는쪽에 mappedBy 걸어주는 것이 좋음
    private List<Member> members = new ArrayList<>(); // team 1 : members N

    public Team(String name) {
        this.name = name;
    }
}
