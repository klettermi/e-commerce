package kr.hhplus.be.server.infrastructure.scheduler;

import kr.hhplus.be.server.application.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TopProductScheduler {

    private final ProductService productService;

    @Scheduled(cron = "0 0 0 */3 * *")
    public void logTopProductsEvery3Days() {
        var top5 = productService.getTopSellingProductsLast3Days(5);
        log.info("[TopProductScheduler] 최근 3일간 최다 판매 상위 5개:");
        top5.forEach(p ->
                log.info("  - {} / {} (가격: {})", p.getId(), p.getItem().getName(), p.getItem().getBasePrice())
        );
    }
}