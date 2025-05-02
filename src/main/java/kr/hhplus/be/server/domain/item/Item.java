package kr.hhplus.be.server.domain.item;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.category.Category;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.common.Money;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "item")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "sale_status")
    private SaleStatus saleStatus;

    @Embedded
    @AttributeOverride(
            name = "amount",
            column = @Column(name = "base_price", nullable = false)
    )
    private Money basePrice;

    @Column(name = "sale_start_date", columnDefinition = "DATETIME")
    private LocalDateTime saleStartDate;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

}
