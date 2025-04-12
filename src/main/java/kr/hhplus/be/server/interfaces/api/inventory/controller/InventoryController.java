package kr.hhplus.be.server.interfaces.api.inventory.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @GetMapping("/{productId}")
    public Map<String, Object> getInventory(@PathVariable Long productId) {
        return Map.of("productId", productId, "stock", 50);
    }
}
