package service;

import dao.RentalDao;
import domain.Rental;
import domain.enums.RentalStatus;
import exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RentalServiceUnitTest {

    @Mock private RentalDao rentalDao;

    @InjectMocks private RentalService rentalService;

    @Test
    public void deleteRentalById_ExistingRental_DeleteSuccess(){
        //given
        Long id = 1L;
        when(rentalDao.deleteById(id)).thenReturn(1);

        //when
        rentalService.deleteRentalById(id);

        //then
        verify(rentalDao).deleteById(id);
    }

    @Test
    public void retrieveAllRentalHistory_ExistingRentals_ReturnRentalList(){
        // given
        Rental rental = Rental.restore(1L, "test@naver.com", 1L, LocalDate.now(), LocalDate.now().plusDays(14), null, RentalStatus.RENTED);

        List<Rental> rentals = List.of(rental);
        when(rentalDao.findAll()).thenReturn(rentals);

        // when
        List<Rental> result = rentalService.retrieveAllRentalHistory();

        // then
        verify(rentalDao).findAll();
        assertThat(result).isSameAs(rentals);
    }

    @Test
    public void retrieveRentalHistoryById_ExistingRental_ReturnRental(){
        //given
        Long id = 1L;
        String userEmail ="test@naver.com";
        Long bookItemId = 1L;
        LocalDate rentedAt = LocalDate.now();
        LocalDate dueDate = rentedAt.plusDays(14);
        LocalDate returnedAt = null;
        RentalStatus status = RentalStatus.RENTED;

        Rental rental = Rental.restore(id,userEmail,bookItemId,rentedAt,dueDate,returnedAt,status);
        when(rentalDao.findById(id)).thenReturn(rental);

        //when
        Rental retrieveRental = rentalService.retrieveRentalHistoryById(id);

        //then
        verify(rentalDao).findById(id);
        assertThat(retrieveRental).isSameAs(rental);
    }

    @Test
    public void retrieveRentalHistoryById_NonExistentRental_ThrowEntityNotFoundException(){
        //given
        Long id = 1L;

        when(rentalDao.findById(id)).thenThrow(EntityNotFoundException.class);

        //when && then
        assertThatThrownBy(()->rentalService.retrieveRentalHistoryById(id)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void retrieveUserRentalHistory_ExistingUser_ReturnRentalList(){
        //given
        Long id1 = 1L;
        String userEmail ="test@naver.com";
        Long bookItemId1 = 1L;
        LocalDate rentedAt1 = LocalDate.now();
        LocalDate dueDate1 = rentedAt1.plusDays(14);
        LocalDate returnedAt1 = null;
        RentalStatus status1 = RentalStatus.RENTED;

        Rental rental1 = Rental.restore(id1,userEmail,bookItemId1,rentedAt1,dueDate1,returnedAt1,status1);

        Long id2 = 2L;
        Long bookItemId2 = 2L;
        LocalDate rentedAt2 = LocalDate.now();
        LocalDate dueDate2 = rentedAt2.plusDays(14);
        LocalDate returnedAt2 = null;
        RentalStatus status2 = RentalStatus.RENTED;

        Rental rental2 = Rental.restore(id2,userEmail,bookItemId2,rentedAt2,dueDate2,returnedAt2,status2);

        List<Rental> listRentals = List.of(rental1,rental2);
        when(rentalDao.findByUserEmail(userEmail)).thenReturn(listRentals);

        //when
        List<Rental> retrieveRentals = rentalService.retrieveUserRentalHistory(userEmail);

        //then
        assertThat(retrieveRentals).isSameAs(listRentals);
    }

    @Test
    public void retrieveUserRentalHistory_NonExistentUser_ReturnEmptyList(){
        //given
        String userEmail = "unknown";
        when(rentalDao.findByUserEmail(userEmail)).thenReturn(List.of());

        //when
        List<Rental> retrieveRentals = rentalService.retrieveUserRentalHistory(userEmail);

        //then
        verify(rentalDao).findByUserEmail(userEmail);
        assertThat(retrieveRentals).isEmpty();
    }

    @Test
    public void retrieveBookRentalHistory_ExistingBookItem_ReturnRentalList(){
        //given
        Long id1 = 1L;
        String userEmail1 ="test001@naver.com";
        Long bookItemId = 1L;
        LocalDate rentedAt1 = LocalDate.now();
        LocalDate dueDate1 = rentedAt1.plusDays(14);
        LocalDate returnedAt1 = null;
        RentalStatus status1 = RentalStatus.RENTED;

        Rental rental1 = Rental.restore(id1,userEmail1,bookItemId,rentedAt1,dueDate1,returnedAt1,status1);

        Long id2 = 2L;
        String userEmail2 ="test002@naver.com";
        LocalDate rentedAt2 = LocalDate.now();
        LocalDate dueDate2 = rentedAt2.plusDays(14);
        LocalDate returnedAt2 = rentedAt2.plusDays(10);
        RentalStatus status2 = RentalStatus.RETURNED;

        Rental rental2 = Rental.restore(id2,userEmail2,bookItemId,rentedAt2,dueDate2,returnedAt2,status2);

        List<Rental> listRentals = List.of(rental1,rental2);
        when(rentalDao.findByBookItemId(bookItemId)).thenReturn(listRentals);

        //when
        List<Rental> retrieveRentals = rentalService.retrieveBookRentalHistory(bookItemId);

        //then
        verify(rentalDao).findByBookItemId(bookItemId);
        assertThat(retrieveRentals).isSameAs(listRentals);
    }

    @Test
    public void retrieveBookRentalHistory_NonExistentBookItem_ReturnEmptyList(){
        //given
        Long bookItemId = 999L;

        when(rentalDao.findByBookItemId(bookItemId)).thenReturn(List.of());

        //when
        List<Rental> retrieveRentals = rentalService.retrieveBookRentalHistory(bookItemId);

        //then
        verify(rentalDao).findByBookItemId(bookItemId);
        assertThat(retrieveRentals).isEmpty();
    }

    @Test
    public void retrieveUserRentedBooks_ActiveRentals_ReturnRentalList(){
        //given
        Long id1 = 1L;
        String userEmail ="test000@naver.com";
        Long bookItemId1 = 1L;
        LocalDate rentedAt1 = LocalDate.now();
        LocalDate dueDate1 = rentedAt1.plusDays(14);
        LocalDate returnedAt1 = null;
        RentalStatus status1 = RentalStatus.RENTED;

        Rental rental1 = Rental.restore(id1,userEmail,bookItemId1,rentedAt1,dueDate1,returnedAt1,status1);

        Long id2 = 2L;
        Long bookItemId2 = 2L;
        LocalDate rentedAt2 = LocalDate.now();
        LocalDate dueDate2 = rentedAt2.plusDays(14);
        LocalDate returnedAt2 = null;
        RentalStatus status2 = RentalStatus.RENTED;

        Rental rental2 = Rental.restore(id2,userEmail,bookItemId2,rentedAt2,dueDate2,returnedAt2,status2);

        List<Rental> listRentals = List.of(rental1,rental2);
        when(rentalDao.findActiveRentalsByUserEmail(userEmail)).thenReturn(listRentals);

        //when
        List<Rental> retrieveRentals = rentalService.retrieveUserRentedBooks(userEmail);

        //then
        verify(rentalDao).findActiveRentalsByUserEmail(userEmail);
        assertThat(retrieveRentals).isSameAs(listRentals);
    }

    @Test
    public void retrieveUserRentedBooks_NoActiveRentals_ReturnEmptyList(){
        //given
        String userEmail ="test000@naver.com";
        when(rentalDao.findActiveRentalsByUserEmail(userEmail)).thenReturn(List.of());

        //when
        List<Rental> retrieveRentals = rentalService.retrieveUserRentedBooks(userEmail);

        //then
        verify(rentalDao).findActiveRentalsByUserEmail(userEmail);
        assertThat(retrieveRentals).isEmpty();
    }

    @Test
    public void recoredOverdueHistory_PastDueRental_UpdateStatusToOverdue(){

    }

    @Test
    public void recoredOverdueHistory_NoActiveRental_ThrowEntityNotFoundException(){

    }
}
