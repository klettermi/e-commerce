package kr.hhplus.be.server.domain.item;

import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Optional<Item> findById(Long id);
}
