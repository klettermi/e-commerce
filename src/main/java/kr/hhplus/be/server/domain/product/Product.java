package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.interfaces.api.product.dto.ProductDto;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.option.Option;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@NoArgsConstructor
@Getter
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private Option option;


    // 할인 없음으로 생성
    public Product(Item item, Option option) {
        this.item = item;
        this.option = option;
    }

    // 제품 자체 할인(정액 또는 정률) 포함 생성자
    public Product(Item item, Option option, DiscountType discountType, double discountValue) {
        this.item = item;
        this.option = option;
    }


    // 최종 가격 계산: Item 기본가격 + Option 추가금액
    // 그리고 제품 자체 할인(정액 또는 정률)을 적용
    public double calculateFinalPrice() {
        double total = item.getBasePrice() + option.getAdditionalCost();
        return Math.max(total, 0);
    }

    public ProductDto toDto() {
        double finalPrice = calculateFinalPrice();
        return new ProductDto(item.getId(), item.getName(), option.getName(), finalPrice);
    }
}
