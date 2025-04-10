package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.interfaces.api.product.dto.ProductDto;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.option.Option;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@NoArgsConstructor
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

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false)
    private double discountValue;

    // 할인 없음으로 생성
    public Product(Item item, Option option) {
        this.item = item;
        this.option = option;
        this.discountType = DiscountType.NONE;
        this.discountValue = 0.0;
    }

    // 제품 자체 할인(정액 또는 정률) 포함 생성자
    public Product(Item item, Option option, DiscountType discountType, double discountValue) {
        this.item = item;
        this.option = option;
        this.discountType = discountType;
        this.discountValue = discountValue;
    }

    // 기존 할인 적용 메서드 (정액 할인)
    public Product applyFixedDiscount(double fixedDiscount) {
        return new Product(this.item, this.option, DiscountType.FIXED, fixedDiscount);
    }

    // 기존 할인 적용 메서드 (정률 할인)
    public Product applyPercentDiscount(double percentDiscount) {
        return new Product(this.item, this.option, DiscountType.PERCENT, percentDiscount);
    }

    // 최종 가격 계산: Item 기본가격 + Option 추가금액
    // 그리고 제품 자체 할인(정액 또는 정률)을 적용
    public double calculateFinalPrice() {
        double total = item.getBasePrice() + option.getAdditionalCost();
        switch (discountType) {
            case FIXED:
                total = total - discountValue;
                break;
            case PERCENT:
                total = total * (1 - discountValue);
                break;
            case NONE:
            default:
                break;
        }
        return Math.max(total, 0);
    }

    public ProductDto toDto() {
        double finalPrice = calculateFinalPrice();
        return new ProductDto(item.getId(), item.getName(), option.getName(), finalPrice);
    }
}
