package dao;

import domain.Rental;
import domain.enums.RentalStatus;

import java.time.LocalDate;
import java.util.List;

public interface RentalDao{
    //대여 정보 저장
    void add(Rental rental);
    //대여 정보 전체 삭제
    void deleteAll();
    //대여 정보 아이디 기준 단일 삭제
    void deleteById(Long id);
    //대여 정보 전체 목록 조회
    List<Rental> findAll();
    //대여 정보 아이디 기준 단일 조회
    Rental findById(Long id);
    //대여 정보 유저 아이디 기준 전체 목록 조회
    List<Rental> findByUserId(String userId);
    //대여 정보 책 아이디 기준 전체 목록 조회
    List<Rental> findByBookItemId(Long bookItemId);
    //반납 해야되는 책 대여 정보 조회
    Rental findActiveRentalByBookItemId(Long bookItemId);
    //유저가 반납해야되는 책 리스트 목록 조회
    List<Rental> findRentedBookByUserId(String userId);

    //반납한 날짜 정보 업데이트
    void updateReturnedDate(Long id, LocalDate returnedDate);

    //대여 정보 상태 변경
    void updateStatus(Long id, RentalStatus status);

    //대여 정보 개수 조회
    int getCount();
}
