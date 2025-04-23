package kr.hhplus.be.server.infrastructure.item;

import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final ItemJpaRepository itemRepo;

    @Override
    public Item save(Item item) {
        return itemRepo.save(item);
    }

    @Override
    public Optional<Item> findById(Long id) {
        return itemRepo.findById(id);
    }
}
