package kr.ac.hansung.kjh.bookrental.service;

import kr.ac.hansung.kjh.bookrental.domain.enums.RentalStatus;
import kr.ac.hansung.kjh.bookrental.entity.RentalEntity;
import kr.ac.hansung.kjh.bookrental.repository.RentalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    //모든 대여 정보 삭제
    public void deleteAll() {
        rentalRepository.deleteAll();
    }

    //대여 정보 아이디 기준 단일 삭제
    public void deleteRentalById(Long id) {
        rentalRepository.deleteById(id);
    }

    //모든 대여 정보 조회
    public List<RentalEntity> retrieveAllRentalHistory() {
        return rentalRepository.findAll();
    }

    //대여 정보 아이디 기준 단일 조회
    public RentalEntity retrieveRentalHistoryById(Long id) {
        return rentalRepository.findById(id).orElse(null);
    }

    //유저가 대여했던 정보 전체 조회
    public List<RentalEntity> retrieveUserRentalHistory(String userEmail) {
        return rentalRepository.findByUserEmail(userEmail);
    }

    //한 도서의 대여 정보 전체 조회
    public List<RentalEntity> retrieveBookRentalHistory(Long bookItemId) {
        return rentalRepository.findByBookItemId(bookItemId);
    }

    //유저가 대여 중인(반납해야 할) 정보 조회
    public List<RentalEntity> retrieveUserRentedBooks(String userEmail) {
        return rentalRepository.findByUserEmailAndStatusIn(userEmail, List.of(RentalStatus.RENTED, RentalStatus.OVERDUE));
    }

    //스케줄러로 자정 넘어기면 OverDue처리
    public void recordOverdueHistory() {
        rentalRepository.updateOverdue(LocalDate.now());
    }
}
