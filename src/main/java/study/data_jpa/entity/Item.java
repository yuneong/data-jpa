package study.data_jpa.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
//@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {

    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

//    protected Item() {} -> @NoArgsConstructor(access = AccessLevel.PROTECTED) 대체 가능

    public Item(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        // @GeneratedValue 를 사용 안할 때
        // implements Persistable<String> 를 사용하여 새로운 엔티티 확인 여부를 직접 구현해야함
        // @EntityListeners(AuditingEntityListener.class) 도 필요함

        return createdDate == null;
    }

}
