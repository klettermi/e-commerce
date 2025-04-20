package kr.hhplus.be.server.infrastructure.option;

import kr.hhplus.be.server.domain.option.OptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OptionRepositoryImpl implements OptionRepository {
    private final OptionJpaRepository optionRepo;
}
