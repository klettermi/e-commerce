package kr.hhplus.be.server.domain.user;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.UserPoint;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @OneToOne(mappedBy = "user")
    private UserPoint userPoint;

    @OneToMany(mappedBy = "user")
    private List<PointHistory> pointHistoryList;

}
