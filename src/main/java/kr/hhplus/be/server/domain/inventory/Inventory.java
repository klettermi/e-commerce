package kr.hhplus.be.server.domain.inventory;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.common.exception.DomainExceptions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static kr.hhplus.be.server.domain.common.exception.DomainExceptions.*;

@Entity
@Table(name = "inventories")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false, unique = true)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    public void decreaseStock(int amount) {
        if (amount < 0) {
            throw new InvalidStateException("감소 수량은 음수가 될 수 없습니다.");
        }
        if (this.quantity < amount) {
            throw new InvalidStateException("재고 부족: productId=" + productId);
        }
        this.quantity -= amount;
    }

    public void increaseStock(int amount) {
        if (amount < 0) {
            throw new InvalidStateException("증가 수량은 음수가 될 수 없습니다.");
        }
        this.quantity += amount;
    }

}

