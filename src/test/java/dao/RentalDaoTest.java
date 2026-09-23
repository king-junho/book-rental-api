package dao;

import domain.Book;
import domain.BookItem;
import domain.Rental;
import domain.User;
import domain.enums.RentalStatus;
import exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/rentalDaoTestContext.xml")
public class RentalDaoTest {
    @Autowired
    private RentalDao rentalDao;
    @Autowired
    private BookDao bookDao;
    @Autowired
    private BookItemDao bookItemDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private TestDataCleaner testDataCleaner;

    @BeforeEach
    void setUp() {
        testDataCleaner.cleanUp();
    }

    private List<Rental> createAndSaveRental(int count){
        List<Rental> rentals = new ArrayList<>();

        for(int i=0; i<count; i++){
            String userEmail = "test"+String.format("%03d",i) +"@gmail.com";
            String isbn = "isbn"+String.format("%03d",i);

            userDao.add(new User(userEmail, "hashed_pw", "testName"));
            bookDao.add(new Book(isbn, "testTitle", "testAuthor", "testDesc"));
            bookItemDao.add(isbn);

            //생성된 책 아이템 꺼내오기
            BookItem bookItem = bookItemDao.findByIsbn(isbn).get(0);

            //Rental 저장
            Rental rental = Rental.create(userEmail, bookItem.getId());
            rentalDao.add(rental);

            //저장된 Rental을 다시 조회해서 'DB가 만들어준 진짜 ID'가 포함된 객체를 반환!
            rentals.add(rentalDao.findByUserEmail(userEmail).get(0));
        }
        return rentals;
    }

    @Test
    public void add_NewRental_SaveSuccess(){
        assertThat(rentalDao.findAll()).hasSize(0);
        createAndSaveRental(5);
        assertThat(rentalDao.getCount()).isEqualTo(5);
    }

    @Test
    public void deleteById_ExistingRental_DeleteSuccess(){
        Rental rental = createAndSaveRental(1).get(0);

        assertThat(rentalDao.getCount()).isEqualTo(1);
        rentalDao.deleteById(rental.getId());
        assertThat(rentalDao.getCount()).isEqualTo(0);
    }

    @Test
    public void updateOverdue_PastDueRental_UpdateStatusToOverdue(){
        Rental rental = createAndSaveRental(1).get(0);
        assertThat(rental.getStatus()).isEqualTo(RentalStatus.RENTED);

        rentalDao.updateOverdue(rental.getDueDate().plusDays(1));
        Rental updateRental = rentalDao.findById(rental.getId());

        assertThat(updateRental.getStatus()).isEqualTo(RentalStatus.OVERDUE);
    }

    @Test
    public void findByBookItemId_ExistingRental_ReturnRentalList(){
        Rental rental = createAndSaveRental(1).get(0);
        List<Rental> rentals = rentalDao.findByBookItemId(rental.getBookItemId());

        assertThat(rentals).hasSize(1);
        assertThat(rentals.get(0).getUserEmail()).isEqualTo("test000@gmail.com");
    }

    @Test
    public void findByUserEmail_NonExistentUser_ReturnEmptyList(){
        Rental rental = createAndSaveRental(1).get(0);
        List<Rental> rentals = rentalDao.findByUserEmail("unknown");

        assertThat(rentals).hasSize(0);
    }

    @Test
    public void findById_ExistingRental_ReturnCorrectRental(){
        Rental rental = createAndSaveRental(1).get(0);
        Rental findRental = rentalDao.findById(rental.getId());

        matchRental(rental,findRental);
    }

    @Test
    public void findById_NonExistentRental_ThrowEntityNotFoundException(){
        assertThatThrownBy(()->rentalDao.findById(999L)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void findByUserEmail_ExistingRentals_ReturnRentalList(){
        List<Rental> rentals = createAndSaveRental(10);

        List<Rental> findRentals = rentalDao.findByUserEmail("test000@gmail.com");

        assertThat(findRentals).hasSize(1);

        matchRental(rentals.get(0),findRentals.get(0));
    }

    @Test
    public void findByBookItemId_NonExistentRental_ReturnEmptyList(){
        List<Rental> rentals =rentalDao.findByBookItemId(999L);

        assertThat(rentals).isEmpty();
    }

    @Test
    public void findActiveRentalByBookItemId_RentedRental_ReturnRental(){
        List<Rental> rentals = createAndSaveRental(10);

        Rental findRental = rentalDao.findActiveRentalByBookItemId(rentals.get(0).getBookItemId());

        matchRental(rentals.get(0),findRental);
    }

    @Test
    public void findActiveRentalByBookItemId_OverdueRental_ReturnRental(){
        List<Rental> rentals = createAndSaveRental(10);

        Rental findActiveRental = rentalDao.findActiveRentalByBookItemId(rentals.get(0).getBookItemId());
        matchRental(findActiveRental,rentals.get(0));

        rentalDao.updateOverdue(findActiveRental.getDueDate().plusDays(1));
        Rental updateRental = rentalDao.findActiveRentalByBookItemId(rentals.get(0).getBookItemId());

        assertThat(updateRental).isNotNull();
        assertThat(updateRental.getStatus()).isEqualTo(RentalStatus.OVERDUE);
        assertThat(updateRental.getId()).isEqualTo(findActiveRental.getId());
    }

    @Test
    public void findActiveRentalByBookItemId_ReturnedRental_ThrowEntityNotFoundException(){
        List<Rental> rentals = createAndSaveRental(10);

        rentalDao.updateReturnedDate(rentals.get(0).getId(), LocalDate.now());
        assertThatThrownBy(()->rentalDao.findActiveRentalByBookItemId(rentals.get(0).getBookItemId())).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void updateReturnedDate_ActiveRental_UpdateReturnedDateAndStatus(){
        List<Rental> rentals = createAndSaveRental(10);
        LocalDate now = LocalDate.now();
        rentalDao.updateReturnedDate(rentals.get(0).getId(), now);

        Rental updateRental = rentalDao.findById(rentals.get(0).getId());
        assertThat(updateRental.getReturnedAt()).isEqualTo(now);
        assertThat(updateRental.getStatus()).isEqualTo(RentalStatus.RETURNED);
    }

    @Test
    public void deleteById_NonExistentRental_NoChange(){
        assertThat(rentalDao.deleteById(999L)).isEqualTo(0);
    }

    @Test
    public void getCount_MultipleRentals_ReturnCorrectCount(){
        List<Rental> rentals = createAndSaveRental(10);

        assertThat(rentalDao.getCount()).isEqualTo(rentals.size());
    }

    @Test
    public void updateOverdue_NotPastDueRental_NoChange(){
        Rental rental =  createAndSaveRental(1).get(0);

        assertThat(rentalDao.updateOverdue(rental.getDueDate())).isEqualTo(0);

        assertThat(rentalDao.updateOverdue(rental.getDueDate().plusDays(1))).isEqualTo(1);
    }

    @Test
    public void updateReturnedDate_ReturnedRental_NoChange(){
        Rental rental =   createAndSaveRental(1).get(0);

        assertThat(rentalDao.updateReturnedDate(rental.getId(), LocalDate.now())).isEqualTo(1);

        assertThat(rentalDao.updateReturnedDate(rental.getId(),LocalDate.now())).isEqualTo(0);
    }

    @Test
    public void updateOverdue_ReturnedRental_NoChange(){
        Rental rental =  createAndSaveRental(1).get(0);

        rentalDao.updateReturnedDate(rental.getId(), LocalDate.now());
        assertThat(rentalDao.updateOverdue(rental.getDueDate().plusDays(1))).isEqualTo(0);
    }

    private void matchRental(Rental expected,Rental actual){
        assertThat(expected.getId()).isEqualTo(actual.getId());
        assertThat(expected.getUserEmail()).isEqualTo(actual.getUserEmail());
        assertThat(expected.getBookItemId()).isEqualTo(actual.getBookItemId());
        assertThat(expected.getReturnedAt()).isEqualTo(actual.getReturnedAt());
        assertThat(expected.getDueDate()).isEqualTo(actual.getDueDate());
        assertThat(expected.getRentedAt()).isEqualTo(actual.getRentedAt());
        assertThat(expected.getStatus()).isEqualTo(actual.getStatus());
    }
}
