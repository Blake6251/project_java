package com.project.portal.batch;

import com.project.portal.domain.DailyOrderStat;
import com.project.portal.domain.Order;
import com.project.portal.repository.DailyOrderStatRepository;
import com.project.portal.repository.OrderRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class DailyStatJobConfig {

    private final OrderRepository orderRepository;
    private final DailyOrderStatRepository dailyOrderStatRepository;
    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job dailyStatJob() {
        return new JobBuilder("dailyStatJob", jobRepository)
                .start(dailyStatStep())
                .build();
    }

    @Bean
    public Step dailyStatStep() {
        LocalDate targetDate = LocalDate.now().minusDays(1);
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay().minusNanos(1);
        List<Order> orders = orderRepository.findByCreatedAtBetween(start, end);

        return new StepBuilder("dailyStatStep", jobRepository)
                .<Order, Integer>chunk(50, transactionManager)
                .reader(new ListItemReader<>(orders))
                .processor((ItemProcessor<Order, Integer>) Order::getQuantity)
                .writer(dailyStatWriter(targetDate))
                .build();
    }

    private ItemWriter<Integer> dailyStatWriter(LocalDate targetDate) {
        return chunk -> {
            long count = chunk.getItems().size();
            long quantity = chunk.getItems().stream().mapToLong(Integer::longValue).sum();
            DailyOrderStat stat = dailyOrderStatRepository.findByStatDate(targetDate)
                    .orElse(DailyOrderStat.builder()
                            .statDate(targetDate)
                            .totalOrders(0L)
                            .totalQuantity(0L)
                            .build());
            stat.setTotalOrders(stat.getTotalOrders() + count);
            stat.setTotalQuantity(stat.getTotalQuantity() + quantity);
            dailyOrderStatRepository.save(stat);
        };
    }

    // 留ㅼ씪 ?덈꼍 1???ㅽ뻾
    @Scheduled(cron = "0 0 1 * * *")
    public void runDailyStatJob() throws Exception {
        jobLauncher.run(dailyStatJob(), new org.springframework.batch.core.JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters());
    }
}
