package service;

import dao.RentalDao;
import domain.Rental;
import domain.enums.RentalStatus;

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
        rentalDao.deleteById(id);
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
    public List<Rental> retrieveUserRentalHistory(String userId){
        return rentalDao.findByUserId(userId);
    }

    //한 도서의 대여 정보 전체 조회
    public List<Rental> retrieveBookRentalHistory(Long bookItemId){
        return rentalDao.findByBookItemId(bookItemId);
    }

    //유저가 대여 중인(반납해야 할) 정보 조회
    public List<Rental> retrieveUserRentedBooks(String userId){
        return rentalDao.findRentedBookByUserId(userId);
    }

    //책 대여 정보 남기기
    public void recoredRentalHistory(Long bookItemId, String userId){
        Rental rental = new Rental(null,userId,bookItemId, LocalDate.now(), RentalStatus.RENTED);
        rentalDao.add(rental);
    }

    //책 반납 정보 남기기
    public void recoredReturnHistory(Long bookItemId){
        Rental rental = rentalDao.findActiveRentalByBookItemId(bookItemId);
        rentalDao.updateReturnedDate(rental.getId(),LocalDate.now());
    }

    public void recoredOverdueHistory(Long bookItemId){
        Rental rental = rentalDao.findActiveRentalByBookItemId(bookItemId);
        rentalDao.updateStatus(rental.getId(),RentalStatus.OVERDUE);
    }
}
