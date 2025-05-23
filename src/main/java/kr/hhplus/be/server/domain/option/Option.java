package kr.hhplus.be.server.domain.option;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.common.Money;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`option`")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Option extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column
    private String name;

    @Embedded
    @AttributeOverride(
            name = "amount",
            column = @Column(name = "additional_cost", nullable = false)
    )
    private Money additionalCost;

}
