package kr.ac.hansung.kjh.bookrental.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalRetryFacadeTest {
    @Mock
    private RentalService rentalService;

    @InjectMocks
    private RentalRetryFacade rentalRetryFacade;

    @Test
    @DisplayName("첫 시도에 성공하면 재시도하지 않는다")
    void rentBookWithRetry_callsOnce_whenFirstAttemptSucceeds() {
        // given
        String userEmail = "test@example.com";
        String isbn = "test_001";

        // when
        rentalRetryFacade.rentBookWithRetry(userEmail, isbn);

        // then
        verify(rentalService).rentBook(userEmail, isbn);
    }

    @Test
    @DisplayName("낙관적 락 충돌 후 성공하면 총 두 번 호출하고 종료한다")
    void rentBookWithRetry_callsTwice_whenSecondAttemptSucceeds() {
        // given
        String userEmail = "test@example.com";
        String isbn = "test_001";

        OptimisticLockingFailureException exception = new OptimisticLockingFailureException("낙관적 락 충돌");
        doThrow(exception).doNothing().when(rentalService).rentBook(userEmail, isbn);

        // when
        rentalRetryFacade.rentBookWithRetry(userEmail, isbn);

        // then
        verify(rentalService, times(2)).rentBook(userEmail, isbn);
    }

    @Test
    @DisplayName("재시도 횟수 모두 충돌하면 마지막 낙관적 락 예외를 전파한다")
    void rentBookWithRetry_throwsLastException_whenAllAttemptsFail() {
        // given
        String userEmail = "test@example.com";
        String isbn = "test_001";

        OptimisticLockingFailureException exception = new OptimisticLockingFailureException("낙관적 락");
        doThrow(exception).when(rentalService).rentBook(userEmail, isbn);

        // when & then
        assertThatThrownBy(() -> rentalRetryFacade.rentBookWithRetry(userEmail, isbn)).isInstanceOf(
                OptimisticLockingFailureException.class);

        verify(rentalService, times(3)).rentBook(userEmail, isbn);
    }
}