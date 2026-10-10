package kr.ac.hansung.kjh.bookrental.service;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RentalOverdueScheduler {
    private final RentalService rentalService;

    public RentalOverdueScheduler(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @SchedulerLock(name = "markOverdue", lockAtMostFor = "60s", lockAtLeastFor = "10s")
    public void run() {
        try {
            rentalService.markOverdue();
            log.info("연체 갱신 완료");
        } catch (RuntimeException e) {
            log.error("연체 갱신 실패");
            throw e;
        }
    }
}
