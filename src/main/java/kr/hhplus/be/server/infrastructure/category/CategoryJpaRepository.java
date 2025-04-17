package kr.hhplus.be.server.infrastructure.category;

import kr.hhplus.be.server.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {
}
