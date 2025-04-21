package kr.hhplus.be.server.infrastructure.option;

import kr.hhplus.be.server.domain.option.Option;
import kr.hhplus.be.server.domain.option.OptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OptionRepositoryImpl implements OptionRepository {
    private final OptionJpaRepository optionRepo;

    @Override
    public void save(Option option) {
        optionRepo.save(option);
    }
}
