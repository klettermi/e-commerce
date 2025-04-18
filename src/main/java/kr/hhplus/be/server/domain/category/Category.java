package kr.hhplus.be.server.domain.category;

import jakarta.persistence.*;
import jdk.jfr.Description;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.interfaces.api.category.dto.CategoryDto;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
@NoArgsConstructor
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @OneToMany(mappedBy = "category")
    private List<Item> items = new ArrayList<>();

    public static Category fromDto(CategoryDto categoryDto) {
        Category category = new Category();
        category.name = categoryDto.name();
        return category;
    }

    public void addItem(Item item) {
        items.add(item);
        item.setCategory(this);
    }

    public void removeItem(Item item) {
        items.remove(item);
        item.setCategory(null);
    }
}
