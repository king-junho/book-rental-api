package service;

import dao.RentalDao;
import domain.Rental;
import domain.enums.RentalStatus;
import exception.BookAlreadyReturnedException;
import exception.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;

public class RentalService {

    private final RentalDao rentalDao;

    public RentalService(RentalDao rentalDao) {
        this.rentalDao = rentalDao;
    }
    //모든 대여 정보 삭제
    public void deleteAll(){
        rentalDao.deleteAll();
    }
    //대여 정보 아이디 기준 단일 삭제
    public void deleteRentalById(Long id){
        int count = rentalDao.deleteById(id);
        if(count==0){
            throw new EntityNotFoundException("대여 정보를 찾을 수 없습니다.");
        }
    }
    //모든 대여 정보 조회
    public List<Rental> retrieveAllRentalHistory(){
        return rentalDao.findAll();
    }
    //대여 정보 아이디 기준 단일 조회
    public Rental retrieveRentalHistoryById(Long id){
        return rentalDao.findById(id);
    }

    //유저가 대여했던 정보 전체 조회
    public List<Rental> retrieveUserRentalHistory(String userEmail){
        return rentalDao.findByUserEmail(userEmail);
    }

    //한 도서의 대여 정보 전체 조회
    public List<Rental> retrieveBookRentalHistory(Long bookItemId){
        return rentalDao.findByBookItemId(bookItemId);
    }

    //유저가 대여 중인(반납해야 할) 정보 조회
    public List<Rental> retrieveUserRentedBooks(String userEmail){
        return rentalDao.findActiveRentalsByUserEmail(userEmail);
    }

    //스케줄러로 자정 넘어기면 OverDue처리
    public void recordOverdueHistory(){
        rentalDao.updateOverdue(LocalDate.now());
    }
}
