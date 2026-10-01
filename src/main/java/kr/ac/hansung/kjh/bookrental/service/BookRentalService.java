package kr.ac.hansung.kjh.bookrental.service;

import kr.ac.hansung.kjh.bookrental.domain.enums.BookItemStatus;
import kr.ac.hansung.kjh.bookrental.domain.enums.RentalStatus;
import kr.ac.hansung.kjh.bookrental.entity.BookItemEntity;
import kr.ac.hansung.kjh.bookrental.entity.RentalEntity;
import kr.ac.hansung.kjh.bookrental.exception.BookAlreadyRentedException;
import kr.ac.hansung.kjh.bookrental.exception.BookAlreadyReturnedException;
import kr.ac.hansung.kjh.bookrental.exception.EntityNotFoundException;
import kr.ac.hansung.kjh.bookrental.exception.RentalAccessDeniedException;
import kr.ac.hansung.kjh.bookrental.repository.BookItemRepository;
import kr.ac.hansung.kjh.bookrental.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookRentalService {
    private final RentalRepository rentalRepository;
    private final BookItemRepository bookItemRepository;

    public BookRentalService(RentalRepository rentalRepository, BookItemRepository bookItemRepository) {
        this.rentalRepository = rentalRepository;
        this.bookItemRepository = bookItemRepository;
    }

    @Transactional
    public void rentBook(String userEmail, String isbn) {
        //중복 대여 확인
        if (rentalRepository.existsByRentaled(userEmail, isbn)) {
            throw new BookAlreadyRentedException("이미 대여 중인 도서입니다. ISBN : " + isbn);
        }

        //BookItem 선택
        List<BookItemEntity> availableBookItems = bookItemRepository.findByIsbnAndStatus(isbn, BookItemStatus.AVAILABLE);

        if (availableBookItems.isEmpty()) {
            throw new EntityNotFoundException("대여 가능한 도서가 없습니다. ISBN : " + isbn);
        }

        BookItemEntity bookItemEntity = availableBookItems.get(0);

        //BookItem 상태 변경

        int updated = bookItemRepository.rent(bookItemEntity.getId());

        if (updated == 0) {
            throw new BookAlreadyRentedException("다른 요청에 의해 이미 대여된 도서입니다.");
        }

        LocalDate now = LocalDate.now();
        RentalEntity data = new RentalEntity();

        data.setUserEmail(userEmail);
        data.setBookItemId(bookItemEntity.getId());
        data.setRentedAt(now);
        data.setDueDate(now.plusDays(14));
        data.setReturnedAt(null);
        data.setStatus(RentalStatus.RENTED);

        rentalRepository.save(data);
    }

    @Transactional
    public void returnBook(Long rentalId, String userEmail) {
        //반납 대여 Rental 정보 조회
        RentalEntity rentalEntity = rentalRepository.findById(rentalId).orElseThrow(jakarta.persistence.EntityNotFoundException::new);

        //본인의 대여 기록인지 확인
        if (!rentalEntity.getUserEmail().equals(userEmail)) {
            throw new RentalAccessDeniedException("본인이 대여한 도서만 반납할 수 있습니다.");
        }
        //BookItem 상태 변경
        int updateBookItem = bookItemRepository.returnBook(rentalEntity.getBookItemId());

        if (updateBookItem == 0) {
            throw new BookAlreadyReturnedException("이미 반납된 도서입니다. id : " + rentalEntity.getBookItemId());
        }

        //Rental 반납 기록
        int updateRental = rentalRepository.updateReturnedDate(rentalEntity.getId(), LocalDate.now());

        if (updateRental == 0) {
            throw new BookAlreadyReturnedException("대여 정보가 이미 반납 처리 되었습니다.");
        }

    }
}
