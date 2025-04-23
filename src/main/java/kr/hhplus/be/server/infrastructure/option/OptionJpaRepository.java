package kr.hhplus.be.server.infrastructure.option;

import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.option.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionJpaRepository extends JpaRepository<Option, Long> {
}
