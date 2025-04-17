package kr.hhplus.be.server.domain.category;

import kr.hhplus.be.server.domain.item.Item;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class CategoryConcurrencyTest {

    private static final int THREAD_COUNT = 20;

    @SuppressWarnings("unchecked")
    private List<Item> getItems(Category category) throws Exception {
        Field itemsField = Category.class.getDeclaredField("items");
        itemsField.setAccessible(true);
        return (List<Item>) itemsField.get(category);
    }

    /**
     * 여러 스레드가 동시에 서로 다른 Item 을 addItem() 했을 때
     * 최종 items 크기가 THREAD_COUNT 와 일치해야 합니다.
     */
    @Test
    void concurrentAddItem() throws Exception {
        Category category = new Category();
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch ready = new CountDownLatch(THREAD_COUNT);
        CountDownLatch start = new CountDownLatch(1);

        // 준비
        IntStream.range(0, THREAD_COUNT).forEach(i ->
                executor.submit(() -> {
                    ready.countDown();
                    try {
                        start.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    Item item = new Item();
                    category.addItem(item);
                })
        );

        // 동시에 시작
        ready.await();
        start.countDown();

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        List<Item> items = getItems(category);
        assertEquals(THREAD_COUNT, items.size(),
                "동시 추가 후 items 크기는 " + THREAD_COUNT + "이어야 합니다.");
    }

    /**
     * 동일한 Item 을 여러 스레드가 동시에 addItem() 후 removeItem() 을 호출할 때,
     * 최종적으로 items 리스트가 비어 있어야 합니다.
     */
    @RepeatedTest(5)
    void concurrentAddAndRemoveSameItem() throws Exception {
        Category category = new Category();
        Item shared = new Item();
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch ready = new CountDownLatch(THREAD_COUNT);
        CountDownLatch start = new CountDownLatch(1);

        //prepare
        IntStream.range(0, THREAD_COUNT).forEach(i ->
                executor.submit(() -> {
                    ready.countDown();
                    try {
                        start.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    category.addItem(shared);
                    category.removeItem(shared);
                })
        );

        ready.await();
        start.countDown();

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        List<Item> items = getItems(category);
        assertTrue(items.isEmpty(), "동시 add/remove 후 items 는 비어 있어야 합니다.");
        assertNull(shared.getCategory(), "제거된 후 shared.getCategory() 는 null 이어야 합니다.");
    }
}
