package kr.hhplus.be.server.domain.option;

import kr.hhplus.be.server.domain.common.BaseEntity;

public interface OptionRepository {
    void save(Option<BaseEntity> option);
}
