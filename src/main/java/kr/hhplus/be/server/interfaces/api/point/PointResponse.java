package kr.hhplus.be.server.interfaces.api.point;

import kr.hhplus.be.server.application.point.PointOutput;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

public class PointResponse {

    @Getter
    @Builder
    public static class UserPoint {
        private Long userId;
        private BigDecimal balance;

        public static UserPoint fromOutput(PointOutput.UserPoint out) {
            return UserPoint.builder()
                    .userId(out.getUserId())
                    .balance(out.getBalance().amount())
                    .build();
        }
    }

    @Getter @Builder
    public static class HistoryItem {
        private Long id;
        private String type;
        private BigDecimal amount;

        public static HistoryItem fromOutput(PointOutput.HistoryItem out) {
            return HistoryItem.builder()
                    .id(out.getId())
                    .type(out.getType())
                    .amount(out.getAmount().amount())
                    .build();
        }
    }

    @Getter @Builder
    public static class HistoryList {
        private Long userId;
        private List<HistoryItem> history;

        public static HistoryList fromOutput(PointOutput.HistoryList out) {
            var items = out.getHistory().stream()
                    .map(HistoryItem::fromOutput)
                    .toList();
            return HistoryList.builder()
                    .userId(out.getUserId())
                    .history(items)
                    .build();
        }
    }
}
