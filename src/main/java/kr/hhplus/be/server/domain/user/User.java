package kr.hhplus.be.server.domain.user;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.point.UserPoint;
import kr.hhplus.be.server.infrastructure.user.UserRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    private String username;

    @OneToOne(mappedBy = "user")
    private UserPoint userPoint;

    public static User fromDto(UserRequest userRequest) {
        User user = new User();
        user.id = userRequest.id();
        user.username = userRequest.username();
        return user;
    }
}
