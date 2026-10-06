package kr.ac.hansung.kjh.bookrental.service;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class RentalRetryFacade {
    private static final int MAX_TRY_COUNT = 3;
    private final RentalService rentalService;

    public RentalRetryFacade(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    public void rentBookWithRetry(String userEmail, String isbn) {
        for (int attempt = 1; attempt <= MAX_TRY_COUNT; attempt++) {
            try {
                rentalService.rentBook(userEmail, isbn);
                return;
            } catch (OptimisticLockingFailureException e) {
                if (attempt == MAX_TRY_COUNT) {
                    throw e;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("대여 재시도 중단", ie);
                }
            }
        }

    }
}
