package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.interfaces.api.product.dto.ProductDto;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.option.Option;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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


    public BigDecimal calculateFinalPrice() {
        return item.getBasePrice().add(option.getAdditionalCost());
    }

    public ProductDto toDto() {
        if (item == null) {
            System.err.println("Error: item is null in Product.toDto()");
        }
        if (option == null) {
            System.err.println("Error: option is null in Product.toDto()");
        }
        BigDecimal finalPrice = calculateFinalPrice();
        return new ProductDto(item.getId(), item.getName(), option.getName(), finalPrice);
    }

}
