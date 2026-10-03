package kr.ac.hansung.kjh.bookrental.service;

import kr.ac.hansung.kjh.bookrental.dto.response.RentalResponse;
import kr.ac.hansung.kjh.bookrental.entity.BookItemEntity;
import kr.ac.hansung.kjh.bookrental.entity.RentalEntity;
import kr.ac.hansung.kjh.bookrental.enums.BookItemStatus;
import kr.ac.hansung.kjh.bookrental.exception.EntityNotFoundException;
import kr.ac.hansung.kjh.bookrental.exception.RentalAccessDeniedException;
import kr.ac.hansung.kjh.bookrental.repository.BookItemRepository;
import kr.ac.hansung.kjh.bookrental.repository.RentalRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final BookItemRepository bookItemRepository;

    public RentalService(RentalRepository rentalRepository, BookItemRepository bookItemRepository) {
        this.rentalRepository = rentalRepository;
        this.bookItemRepository = bookItemRepository;
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
            throw new EntityNotFoundException("대여 가능한 도서가 없습니다. isbn: " + isbn);
        }

        BookItemEntity bookItemEntity = availableBookItems.get(0);
        bookItemEntity.rent();

        rentalRepository.save(RentalEntity.create(userEmail, bookItemEntity.getId()));
    }

    @Transactional
    public void returnBook(Long id, String userEmail) {
        RentalEntity rentalEntity = rentalRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("해당 도서 대여 기록이 없습니다. id: " + id));

        //본인의 대여 기록인지 확인
        if (!rentalEntity.getUserEmail().equals(userEmail)) {
            throw new RentalAccessDeniedException("본인이 대여한 도서만 반납할 수 있습니다.");
        }

        BookItemEntity bookItemEntity = bookItemRepository.findById(rentalEntity.getBookItemId()).orElseThrow(
                () -> new EntityNotFoundException("반납할 도서가 존재하지 않습니다."));

        bookItemEntity.returnBook();
        rentalEntity.returnBook(LocalDate.now());
    }

    //스케줄러로 자정 넘어기면 OverDue처리
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void markOverdue() {
        LocalDate today = LocalDate.now();
        rentalRepository.updateOverdue(today);
    }
}