package kr.hhplus.be.server.infrastructure.scheduler;

import kr.hhplus.be.server.application.product.ProductFacade;
import kr.hhplus.be.server.application.product.ProductInput;
import kr.hhplus.be.server.application.product.ProductOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TopProductScheduler {

    private final ProductFacade productFacade;

    @Scheduled(cron = "0 0 0 */3 * *")
    public void logTopProductsEvery3Days() {
        ProductInput.TopSelling input = new ProductInput.TopSelling();
        input.setTopN(5);

        ProductOutput.TopSellingList topList =
                productFacade.getTopSellingProductsLast3Days(input);

        log.info("[TopProductScheduler] 최근 3일간 최다 판매 상위 5개:");
        for (ProductOutput.Item item : topList.getProducts()) {
            log.info("  - {} / {} (가격: {})",
                    item.getId(),
                    item.getName(),
                    item.getBasePrice()
            );
        }
    }
}