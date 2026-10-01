package kr.ac.hansung.kjh.bookrental.service;

import kr.ac.hansung.kjh.bookrental.dao.BookItemDao;
import kr.ac.hansung.kjh.bookrental.dao.RentalDao;
import kr.ac.hansung.kjh.bookrental.domain.BookItem;
import kr.ac.hansung.kjh.bookrental.domain.Rental;
import kr.ac.hansung.kjh.bookrental.exception.BookAlreadyRentedException;
import kr.ac.hansung.kjh.bookrental.exception.BookAlreadyReturnedException;
import kr.ac.hansung.kjh.bookrental.exception.EntityNotFoundException;
import kr.ac.hansung.kjh.bookrental.exception.RentalAccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookRentalService {
    private final BookItemDao bookItemDao;
    private final RentalDao rentalDao;

    public BookRentalService(BookItemDao bookItemDao, RentalDao rentalDao) {
        this.bookItemDao = bookItemDao;
        this.rentalDao = rentalDao;
    }

    @Transactional
    public void rentBook(String userEmail, String isbn) {
        //중복 대여 확인
        if (rentalDao.existsActiveRentalByUserEmailAndIsbn(userEmail, isbn)) {
            throw new BookAlreadyRentedException("이미 대여 중인 도서입니다. ISBN : " + isbn);
        }

        //BookItem 선택
        List<BookItem> availableBooks = bookItemDao.findAvailableBooks(isbn);

        if (availableBooks.isEmpty()) {
            throw new EntityNotFoundException("대여 가능한 도서가 없습니다. ISBN : " + isbn);
        }

        BookItem bookItem = availableBooks.get(0);

        //BookItem 상태 변경
        int updated = bookItemDao.rent(bookItem.getId());

        if (updated == 0) {
            throw new BookAlreadyRentedException("다른 요청에 의해 이미 대여된 도서입니다.");
        }

        Rental rental = Rental.create(userEmail, bookItem.getId());

        rentalDao.add(rental);
    }

    @Transactional
    public void returnBook(Long rentalId, String userEmail) {
        //반납 대여 Rental 정보 조회
        Rental rental = rentalDao.findById(rentalId);

        //본인의 대여 기록인지 확인

        if (!rental.getUserEmail().equals(userEmail)) {
            throw new RentalAccessDeniedException("본인이 대여한 도서만 반납할 수 있습니다.");
        }
        //BookItem 상태 변경
        int updateBookItem = bookItemDao.returnBook(rental.getBookItemId());

        if (updateBookItem == 0) {
            throw new BookAlreadyReturnedException("이미 반납된 도서입니다. id : " + rental);
        }

        //Rental 반납 기록
        int updateRental = rentalDao.updateReturnedDate(rental.getId(), LocalDate.now());

        if (updateRental == 0) {
            throw new BookAlreadyReturnedException("대여 정보가 이미 반납 처리 되었습니다.");
        }

    }
}
