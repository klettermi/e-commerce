package kr.hhplus.be.server.domain.category;

import kr.hhplus.be.server.domain.item.Item;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {
    @SuppressWarnings("unchecked")
    private List<Item> getItems(Category category) throws Exception {
        Field itemsField = Category.class.getDeclaredField("items");
        itemsField.setAccessible(true);
        return (List<Item>) itemsField.get(category);
    }

    @Test
    void testAddItem() throws Exception {
        Category category = new Category();
        Item item = new Item();

        category.addItem(item);

        List<Item> items = getItems(category);
        assertEquals(1, items.size(), "카테고리에 아이템이 1개 추가되어야 합니다.");
        assertSame(item, items.get(0), "추가한 아이템과 목록에 있는 아이템은 동일해야 합니다.");
        assertEquals(category, item.getCategory(), "아이템의 category 참조가 올바르게 설정되어야 합니다.");
    }

    @Test
    void testRemoveItem() throws Exception {
        Category category = new Category();
        Item item = new Item();
        category.addItem(item);

        category.removeItem(item);

        List<Item> items = getItems(category);
        assertTrue(items.isEmpty(), "아이템이 제거되어야 하므로 카테고리의 items는 비어있어야 합니다.");
        assertNull(item.getCategory(), "아이템의 category 참조가 null로 설정되어야 합니다.");
    }
}