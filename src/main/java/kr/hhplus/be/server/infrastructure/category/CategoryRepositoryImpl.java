package kr.hhplus.be.server.infrastructure.category;

import kr.hhplus.be.server.domain.category.Category;
import kr.hhplus.be.server.domain.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository categoryRepo;

    @Override
    public Category save(Category category) {
        return categoryRepo.save(category);
    }
}
