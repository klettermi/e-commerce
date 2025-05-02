package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.common.Money;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
public class PointOutput {

    @Getter
    @Builder
    public static class UserPoint {
        private Long userId;
        private Money balance;
    }

    @Getter
    @Builder
    public static class HistoryItem {
        private Long id;
        private String type;
        private Money amount;
    }

    @Getter
    @Builder
    public static class HistoryList {
        private Long userId;
        private List<HistoryItem> history;
    }
}
