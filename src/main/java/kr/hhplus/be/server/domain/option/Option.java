package kr.hhplus.be.server.domain.option;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.Money;
import kr.hhplus.be.server.interfaces.api.option.OptionRequest;
import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "options")
@Getter
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

    public static Option fromDto(OptionRequest dto) {
        Option option = new Option();
        option.name = dto.name();
        option.additionalCost = dto.additionalCost();
        return option;
    }
}
