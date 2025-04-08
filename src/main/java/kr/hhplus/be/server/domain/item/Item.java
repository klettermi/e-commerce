package kr.hhplus.be.server.domain.item;

import jakarta.persistence.*;
import kr.hhplus.be.server.api.item.dto.ItemDto;
import kr.hhplus.be.server.domain.category.Category;
import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "items")
@Getter
@NoArgsConstructor
public class Item extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "sale_status")
    private SaleStatus saleStatus;

    @Column(name = "base_price", nullable = false)
    private Integer basePrice;

    @Column(name = "sale_start_date", columnDefinition = "DATETIME")
    private LocalDateTime saleStartDate;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public static Item fromDto(ItemDto dto, Category category) {
        Item item = new Item();
        item.name = dto.name();
        item.description = dto.description();
        item.saleStatus = dto.saleStatus();
        item.basePrice = dto.basePrice();
        item.saleStartDate = dto.saleStartDate();
        item.setCategory(category);
        return item;
    }

}
