package kr.ac.hansung.kjh.bookrental.service;

import kr.ac.hansung.kjh.bookrental.config.ShedLockConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalOverdueSchedulerTest {
    @Mock
    private RentalService rentalService;

    @InjectMocks
    private RentalOverdueScheduler scheduler;

    @Test
    @DisplayName("스케줄 작업 실행 시 연체 갱신을 요청한다")
    void run_requestsOverdueUpdate() {
        // when
        scheduler.run();

        // then
        verify(rentalService).markOverdue();
    }

    @Test
    @DisplayName("연체 갱신에 실패하면 예외를 던진다")
    void run_propagatesException_whenUpdateFails() {
        // given
        RuntimeException failuer = new RuntimeException("연체 갱신 실패");

        doThrow(failuer).when(rentalService).markOverdue();

        // when & then
        assertThatThrownBy(() -> scheduler.run()).isSameAs(failuer);
        verify(rentalService).markOverdue();
    }

    @Test
    @DisplayName("서버 A가 연체 갱신 중이면 서버 B는 같은 작업을 건너뛴다")
    void run_skipsSecondServer_whenFirstServerHoldsLock() throws Exception {
        // given
        String databaseUrl = "jdbc:h2:mem:shedlock_" + UUID.randomUUID();

        RentalService serviceA = mock(RentalService.class);
        RentalService serviceB = mock(RentalService.class);

        CountDownLatch taskStarted = new CountDownLatch(1);
        CountDownLatch allowTaskToFinish = new CountDownLatch(1);

        doAnswer(invocation -> {
            taskStarted.countDown();

            if (!allowTaskToFinish.await(15, SECONDS)) {
                throw new AssertionError("서버 A의 작업 종료 신호를 받지 못했습니다.");
            }
            return null;
        }).when(serviceA).markOverdue();

        try (Connection keeper = DriverManager.getConnection(databaseUrl, "sa", "")) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(createDataSource(databaseUrl));

            jdbcTemplate.execute("""
                        create table shedlock(
                            name varchar(64) primary key ,
                            lock_until timestamp(3) not null ,
                            locked_at timestamp(3) not null ,
                            locked_by varchar(255) not null 
                        )
                    """);

            try (AnnotationConfigApplicationContext serverA = createServer(databaseUrl,
                    serviceA); AnnotationConfigApplicationContext serverB = createServer(databaseUrl, serviceB);) {
                RentalOverdueScheduler schedulerA = serverA.getBean(RentalOverdueScheduler.class);
                RentalOverdueScheduler schedulerB = serverB.getBean(RentalOverdueScheduler.class);
                ExecutorService executor = Executors.newFixedThreadPool(2);

                // when
                try {
                    Future<?> executionA = executor.submit(schedulerA::run);

                    assertThat(taskStarted.await(5, SECONDS)).as("서버 A가 연체 갱신 작업에 진입했는지").isTrue();

                    Future<?> executionB = executor.submit(schedulerB::run);
                    executionB.get(5, SECONDS);

                    // then
                    verify(serviceA, times(1)).markOverdue();
                    verifyNoInteractions(serviceB);

                    allowTaskToFinish.countDown();
                    executionA.get(5, SECONDS);
                } finally {
                    allowTaskToFinish.countDown();
                    executor.shutdown();

                    assertThat(executor.awaitTermination(5, SECONDS)).as("테스트 작업 스레드가 종료됐는지").isTrue();
                }
            }
        }
    }

    private static AnnotationConfigApplicationContext createServer(String databaseUrl, RentalService service) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        try {
            context.registerBean(DataSource.class, () -> createDataSource(databaseUrl));

            context.registerBean(RentalService.class, () -> service);

            context.register(ShedLockConfig.class, RentalOverdueScheduler.class);

            context.refresh();
            return context;
        } catch (RuntimeException | Error e) {
            context.close();
            throw e;
        }
    }

    private static DataSource createDataSource(String databaseUrl) {
        return new DriverManagerDataSource(databaseUrl, "sa", "");
    }
}
