package domain;

import domain.enums.RentalStatus;
import java.time.LocalDate;

public class Rental {
    private final Long id;
    private final String userEmail;
    private final Long bookItemId;

    private final LocalDate rentedAt;
    private final LocalDate dueDate;

    private LocalDate returnedAt;
    private RentalStatus status;

    private Rental(Long id, String userEmail, Long bookItemId, LocalDate rentedAt, LocalDate dueDate, LocalDate returnedAt, RentalStatus status) {
        validate(userEmail,bookItemId,rentedAt,dueDate,returnedAt,status);

        this.id = id;
        this.userEmail = userEmail;
        this.bookItemId = bookItemId;
        this.rentedAt = rentedAt;
        this.dueDate = dueDate;
        this.returnedAt = returnedAt;
        this.status = status;
    }

    //신규 대여 정보 생성
    public static Rental create(String userEmail, Long bookItemId){
        LocalDate rentedAt = LocalDate.now();

        return new Rental(null,userEmail,bookItemId,rentedAt,rentedAt.plusDays(14),null,RentalStatus.RENTED);
    }

    public static Rental restore(Long id, String userEmail, Long bookItemId, LocalDate rentedAt, LocalDate dueDate,LocalDate returnedAt,RentalStatus status){
        if(id==null)
            throw new IllegalArgumentException("id는 Null일 수 없습니다.");
        return new Rental(id,userEmail,bookItemId,rentedAt,dueDate,returnedAt,status);
    }

    private void validate(String userEmail, Long bookItemId, LocalDate rentedAt, LocalDate dueDate, LocalDate returnedAt, RentalStatus status){
        if(userEmail==null){
            throw new IllegalArgumentException("userEmail은 null일 수 없습니다.");
        }
        if(bookItemId==null){
            throw new IllegalArgumentException("bookItemId는 null일 수 없습니다.");
        }
        if(rentedAt == null){
            throw new IllegalArgumentException("대여일은 null일 수 없습니다.");
        }
        if(dueDate==null){
            throw new IllegalArgumentException("반납 예정일은 null일 수 없습니다.");
        }
        if(status==null){
            throw new IllegalArgumentException("대여 상태는 null일 수 없습니다.");
        }
        if(status==RentalStatus.RETURNED&&returnedAt==null){
            throw new IllegalArgumentException("반납 완료 상태라면 반납일이 존재해야 합니다.");
        }
        if(status!=RentalStatus.RETURNED && returnedAt!=null) {
            throw new IllegalArgumentException("반납 완료 상태가 아니라면 반납일이 존재할 수 없습니다.");
        }
        if(rentedAt.isAfter(dueDate)){
            throw new IllegalArgumentException("대여일이 반납 예정일보다 늦을 수 없습니다.");
        }
        if(returnedAt!=null && returnedAt.isBefore(rentedAt)){
            throw new IllegalArgumentException("대여일이 반납일보다 늦을 수 없습니다.");
        }
    }

    public Long getId() {
        return this.id;
    }
    public String getUserEmail() {
        return this.userEmail;
    }
    public Long getBookItemId() {
        return this.bookItemId;
    }
    public LocalDate getRentedAt() {
        return this.rentedAt;
    }
    public LocalDate getDueDate() {
        return this.dueDate;
    }
    public LocalDate getReturnedAt() {
        return this.returnedAt;
    }
    public RentalStatus getStatus() {
        return this.status;
    }

    public void returnRental(){
        if(status==RentalStatus.RETURNED){
            throw new IllegalStateException("이미 반납된 대여입니다.");
        }
        this.returnedAt = LocalDate.now();
        this.status = RentalStatus.RETURNED;
    }

    public void markOverdue(){
        if(status==RentalStatus.RETURNED){
            return;
        }
        if(dueDate.isBefore(LocalDate.now())){
            this.status = RentalStatus.OVERDUE;
        }
    }
}

