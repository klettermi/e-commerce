package kr.hhplus.be.server.domain.option;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kr.hhplus.be.server.api.option.dto.OptionDto;
import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "options")
@Getter
@NoArgsConstructor
public class Option extends BaseEntity{
    @Column
    private String name;

    @Column
    private Integer additionalCost;

    public static Option fromOption(OptionDto dto) {
        Option option = new Option();
        option.name = dto.name();
        option.additionalCost = dto.additionalCost();
        return option;
    }
}
