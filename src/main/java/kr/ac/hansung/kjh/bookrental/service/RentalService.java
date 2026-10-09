package kr.ac.hansung.kjh.bookrental.service;

import kr.ac.hansung.kjh.bookrental.dto.response.RentalResponse;
import kr.ac.hansung.kjh.bookrental.entity.BookItemEntity;
import kr.ac.hansung.kjh.bookrental.entity.RentalEntity;
import kr.ac.hansung.kjh.bookrental.enums.BookItemStatus;
import kr.ac.hansung.kjh.bookrental.exception.CustomException;
import kr.ac.hansung.kjh.bookrental.exception.ErrorCode;
import kr.ac.hansung.kjh.bookrental.repository.BookItemRepository;
import kr.ac.hansung.kjh.bookrental.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final BookItemRepository bookItemRepository;
    private final Clock clock;

    public RentalService(RentalRepository rentalRepository, BookItemRepository bookItemRepository, Clock clock) {
        this.rentalRepository = rentalRepository;
        this.bookItemRepository = bookItemRepository;
        this.clock = clock;
    }

    //유저가 대여했던 정보 전체 조회
    @Transactional(readOnly = true)
    public List<RentalResponse> retrieveUserRentalHistory(String userEmail) {
        return rentalRepository.findByUserEmail(userEmail).stream().map(RentalResponse::from).toList();
    }

    @Transactional
    public void rentBook(String userEmail, String isbn) {
        //대여할 BookItem 선택
        List<BookItemEntity> availableBookItems = bookItemRepository.findByIsbnAndStatus(isbn,
                BookItemStatus.AVAILABLE);

        if (availableBookItems.isEmpty()) {
            throw new CustomException(ErrorCode.BOOK_OUT_OF_STOCK);
        }

        BookItemEntity bookItemEntity = availableBookItems.get(0);
        bookItemEntity.rent();
        LocalDate rentedAt = LocalDate.now(clock);

        rentalRepository.save(RentalEntity.create(userEmail, bookItemEntity.getId(), rentedAt));
    }

    @Transactional
    public void returnBook(Long id, String userEmail) {
        RentalEntity rentalEntity = rentalRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.RENTAL_NOT_FOUND));

        //본인의 대여 기록인지 확인
        if (!rentalEntity.getUserEmail().equals(userEmail)) {
            throw new CustomException(ErrorCode.RENTAL_ACCESS_DENIED);
        }

        BookItemEntity bookItemEntity = bookItemRepository.findById(rentalEntity.getBookItemId()).orElseThrow(
                () -> new CustomException(ErrorCode.BOOKITEM_NOT_FOUND));

        bookItemEntity.returnBook();
        LocalDate returnAt = LocalDate.now(clock);
        rentalEntity.returnBook(returnAt);
    }

    //스케줄러로 자정 넘어기면 OverDue처리
    @Transactional
    public void markOverdue() {
        LocalDate today = LocalDate.now(clock);
        rentalRepository.updateOverdue(today);
    }
}