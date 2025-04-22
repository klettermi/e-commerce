package kr.hhplus.be.server.interfaces.api.user;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public Map<String, Object> getUser(@PathVariable Long id) {
        return Map.of("id", id, "email", "user" + id + "@example.com");
    }
}
