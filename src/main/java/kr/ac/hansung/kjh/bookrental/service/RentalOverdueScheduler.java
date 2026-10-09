package kr.ac.hansung.kjh.bookrental.service;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RentalOverdueScheduler {
    private final RentalService rentalService;

    public RentalOverdueScheduler(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @SchedulerLock(name = "markOverdue", lockAtMostFor = "30s", lockAtLeastFor = "10s")
    public void run() {

        long startedAt = System.nanoTime();

        try {
            rentalService.markOverdue();
            log.info("연체 갱신 완료: elapsedMs={}", elapsedMillis(startedAt));
        } catch (RuntimeException e) {
            log.error("연체 갱신 실패: elapsedMs={}", elapsedMillis(startedAt), e);
            throw e;
        }
    }

    private static long elapsedMillis(long startedAt) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);
    }
}
