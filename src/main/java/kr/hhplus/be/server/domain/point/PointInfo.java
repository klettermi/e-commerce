package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.common.Money;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class PointInfo {

    @Getter
    @Builder
    public static class UserPointInfo {
        private Long userId;
        private Money balance;
    }

    @Getter
    @Builder
    public static class HistoryItemInfo {
        private Long id;
        private String type;     // CHARGE 또는 USE
        private Money amount;
    }

    @Getter
    @Builder
    public static class HistoryListInfo {
        private Long userId;
        private List<HistoryItemInfo> history;
    }

}