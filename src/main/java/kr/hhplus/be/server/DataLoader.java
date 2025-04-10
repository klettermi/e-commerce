package kr.hhplus.be.server;

import kr.hhplus.be.server.domain.category.Category;
import kr.hhplus.be.server.domain.category.CategoryRepository;
import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.ItemRepository;
import kr.hhplus.be.server.domain.item.SaleStatus;
import kr.hhplus.be.server.domain.option.Option;
import kr.hhplus.be.server.domain.option.OptionRepository;
import kr.hhplus.be.server.domain.point.UserPoint;
import kr.hhplus.be.server.domain.point.UserPointRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.category.dto.CategoryDto;
import kr.hhplus.be.server.interfaces.api.item.dto.ItemDto;
import kr.hhplus.be.server.interfaces.api.option.dto.OptionDto;
import kr.hhplus.be.server.interfaces.api.point.dto.UserPointRequestDto;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;

@Configuration
@Profile({"local", "test"})  // local 및 test 프로파일에서만 실행
public class DataLoader {

    @Bean
    public CommandLineRunner seedData(UserRepository userRepository,
                                      UserPointRepository userPointRepository,
                                      ProductRepository productRepository,
                                      ItemRepository itemRepository,
                                      OptionRepository optionRepository,
                                      CategoryRepository categoryRepository) {
        return args -> {
            // 사용자 및 관련 UserPoint 시딩
            if (userRepository.count() == 0) {
                User user = new User();
                User managedUser = userRepository.saveAndFlush(user);

                UserPointRequestDto userPointRequestDto = new UserPointRequestDto(managedUser.id, 100000);
                UserPoint userPoint = new UserPoint();
                userPointRepository.save(userPoint);
            }

            // 상품 시딩
            if (productRepository.count() == 0) {
                CategoryDto categoryDto = new CategoryDto("일반");
                Category category = Category.fromDto(categoryDto);
                categoryRepository.save(category);

                ItemDto itemDto = new ItemDto(
                        "AirForce",
                        "AirForce Description",
                        SaleStatus.ON_SALE,
                        100000,
                        LocalDateTime.now()
                );
                OptionDto optionDto = new OptionDto(
                        "White240",
                        5000
                );

                Item item = Item.fromDto(itemDto, category);
                itemRepository.save(item);
                Option option = Option.fromDto(optionDto);
                optionRepository.save(option);
                Product product = new Product(item, option);
                productRepository.save(product);
            }
        };
    }
}
