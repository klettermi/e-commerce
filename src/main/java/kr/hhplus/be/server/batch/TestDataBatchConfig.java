package kr.hhplus.be.server.batch;

import kr.hhplus.be.server.domain.category.Category;
import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.option.Option;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.inventory.Inventory;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.category.CategoryJpaRepository;
import kr.hhplus.be.server.infrastructure.item.ItemJpaRepository;
import kr.hhplus.be.server.infrastructure.option.OptionJpaRepository;
import kr.hhplus.be.server.infrastructure.product.ProductJpaRepository;
import kr.hhplus.be.server.infrastructure.inventory.InventoryJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
@EnableBatchProcessing
@Profile("test")
@RequiredArgsConstructor
public class TestDataBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager txManager;
    private final TestDataSeedTasklet seedTasklet;

    // --- JPA 리포지토리들 ---
    private final UserJpaRepository      userRepo;
    private final CategoryJpaRepository  categoryRepo;
    private final ItemJpaRepository      itemRepo;
    private final OptionJpaRepository    optionRepo;
    private final ProductJpaRepository   productRepo;
    private final InventoryJpaRepository inventoryRepo;

    // 1) 기존 Tasklet
    @Bean
    public Step seedStep() {
        return new StepBuilder("seedStep", jobRepository)
                .tasklet(seedTasklet, txManager)
                .build();
    }

    // 2) User bulk insert
    @Bean
    public Step bulkUserStep() {
        return new StepBuilder("bulkUserStep", jobRepository)
                .<User, User>chunk(100, txManager)
                .reader(reader(User.class, 500))
                .writer(writer(userRepo::saveAll))
                .build();
    }

    // 3) Category bulk insert
    @Bean
    public Step bulkCategoryStep() {
        return new StepBuilder("bulkCategoryStep", jobRepository)
                .<Category, Category>chunk(50, txManager)
                .reader(reader(Category.class, 10))  // 예: 10개
                .writer(writer(categoryRepo::saveAll))
                .build();
    }

    // 4) Item bulk insert
    @Bean
    public Step bulkItemStep() {
        return new StepBuilder("bulkItemStep", jobRepository)
                .<Item, Item>chunk(50, txManager)
                .reader(reader(Item.class, 100))
                .writer(writer(itemRepo::saveAll))
                .build();
    }

    // 5) Option bulk insert
    @Bean
    public Step bulkOptionStep() {
        return new StepBuilder("bulkOptionStep", jobRepository)
                .<Option, Option>chunk(50, txManager)
                .reader(reader(Option.class, 100))
                .writer(writer(optionRepo::saveAll))
                .build();
    }

    // 6) Product bulk insert
    @Bean
    public Step bulkProductStep() {
        return new StepBuilder("bulkProductStep", jobRepository)
                .<Product, Product>chunk(50, txManager)
                .reader(reader(Product.class, 200))
                .writer(writer(productRepo::saveAll))
                .build();
    }

    // 7) Inventory bulk insert
    @Bean
    public Step bulkInventoryStep() {
        return new StepBuilder("bulkInventoryStep", jobRepository)
                .<Inventory, Inventory>chunk(50, txManager)
                .reader(reader(Inventory.class, 200))
                .writer(writer(inventoryRepo::saveAll))
                .build();
    }

    // 8) Job 흐름에 모두 연결
    @Bean
    public Job testDataJob() {
        return new JobBuilder("testDataJob", jobRepository)
                .start(seedStep())
                .next(bulkUserStep())
                .next(bulkCategoryStep())
                .next(bulkItemStep())
                .next(bulkOptionStep())
                .next(bulkProductStep())
                .next(bulkInventoryStep())
                .build();
    }

    // ----------------------------
    // 범용 Instancio Reader
    // ----------------------------
    private <T> ItemReader<T> reader(Class<T> type, int totalCount) {
        List<T> list = IntStream.range(0, totalCount)
                .mapToObj(i -> Instancio.create(type))
                .collect(Collectors.toList());
        return new IteratorItemReader<>(list);
    }

    // ----------------------------
    // 범용 Repository Writer
    // ----------------------------
    private <T> ItemWriter<T> writer(java.util.function.Consumer<List<T>> saveAllFn) {
        return items -> saveAllFn.accept((List<T>) items);
    }
}
