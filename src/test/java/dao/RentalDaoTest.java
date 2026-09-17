package dao;

import domain.Book;
import domain.BookItem;
import domain.Rental;
import domain.User;
import domain.enums.RentalStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

    @BeforeEach
    void setUp() {
        rentalDao.deleteAll();
        bookItemDao.deleteAll();
        bookDao.deleteAll();
        userDao.deleteAll();
    }

    private Rental createAndSaveRental(){
        String userId = "test0@gmail.com";
        String bookId = "isbn0";

        //부모 데이터(유저, 책, 책 아이템) 생성
        userDao.add(new User(userId, "hashed_pw", "testName"), "hashed_pw");
        bookDao.add(new Book(bookId, "testTitle", "testAuthor", "testDesc"));
        bookItemDao.add(bookId);

        //생성된 책 아이템 꺼내오기
        BookItem bookItem = bookItemDao.findByIsbn(bookId).get(0);

        //Rental 저장
        Rental rental = new Rental(null, userId, bookItem.getId(), LocalDate.now(), RentalStatus.RENTED);
        rentalDao.add(rental);

        //저장된 Rental을 다시 조회해서 'DB가 만들어준 진짜 ID'가 포함된 객체를 반환!
        return rentalDao.findByUserId(userId).get(0);
    }

    @Test
    public void add_NewRental_SaveSuccess(){
        assertThat(rentalDao.findAll()).hasSize(0);
        createAndSaveRental();
        assertThat(rentalDao.getCount()).isEqualTo(1);
    }

    @Test
    public void deleteById_ExistingRental_DeleteSuccess(){
        Rental rental = createAndSaveRental();

        assertThat(rentalDao.getCount()).isEqualTo(1);
        rentalDao.deleteById(rental.getId());
        assertThat(rentalDao.getCount()).isEqualTo(0);
    }

    @Test
    public void updateStatus_ValidId_UpdatesStatus(){
        Rental rental = createAndSaveRental();
        assertThat(rental.getStatus()).isEqualTo(RentalStatus.RENTED);

        rentalDao.updateStatus(rental.getId(), RentalStatus.RETURNED);
        Rental updateRental = rentalDao.findById(rental.getId());

        assertThat(updateRental.getStatus()).isEqualTo(RentalStatus.RETURNED);
    }

    @Test
    public void updateReturnDate_ValidId_UpdatesReturnedDate(){
        Rental rental = createAndSaveRental();
        assertThat(rental.getReturnedAt()).isNull();

        LocalDate returnDate = LocalDate.now();
        rentalDao.updateReturnedDate(rental.getId(), returnDate);
        Rental updateRental = rentalDao.findById(rental.getId());
        assertThat(updateRental.getReturnedAt()).isEqualTo(returnDate);
    }

    @Test
    public void findByBookItemId_ValidId_ReturnsRetalList(){
        Rental rental = createAndSaveRental();
        List<Rental> rentals = rentalDao.findByBookItemId(rental.getBookItemId());

        assertThat(rentals).hasSize(1);
        assertThat(rentals.get(0).getUserId()).isEqualTo("test0@gmail.com");
    }

    @Test
    public void findByUserId_InValidId_ReturnsEmptyList(){
        Rental rental = createAndSaveRental();
        List<Rental> rentals = rentalDao.findByUserId("unknown");

        assertThat(rentals).hasSize(0);
    }
}
