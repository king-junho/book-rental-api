package dao;

import domain.Rental;
import domain.enums.RentalStatus;
import exception.EntityNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import java.time.LocalDate;
import java.util.List;

public class RentalDaoJdbc implements RentalDao {
    private final JdbcTemplate jdbcTemplate;

    public RentalDaoJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Rental> rentalMapper = (rs, rowNum)->{
        Long id = rs.getLong("id");
        String userEmail = rs.getString("user_email");
        Long bookItemId = rs.getLong("book_item_id");
        LocalDate rentedAt = rs.getObject("rented_at",LocalDate.class);
        LocalDate dueDate = rs.getObject("due_date", LocalDate.class);
        LocalDate returnedAt = rs.getObject("returned_at",LocalDate.class);
        RentalStatus status = RentalStatus.valueOf(rs.getString("status"));

        Rental rental = Rental.restore(id,userEmail,bookItemId,rentedAt,dueDate,returnedAt,status);

        return rental;
    };

    @Override
    public void add(Rental rental) {
        String sql = "insert into rentals(user_email, book_item_id, rented_at, due_date, returned_at,status) values(?,?,?,?,?,?)";

        String userEmail = rental.getUserEmail();
        Long bookItemId = rental.getBookItemId();
        LocalDate rentedAt = rental.getRentedAt();
        LocalDate dueDate = rental.getDueDate();
        LocalDate returnedAt = rental.getReturnedAt();
        RentalStatus status = rental.getStatus();

        jdbcTemplate.update(sql,userEmail,bookItemId,rentedAt,dueDate,returnedAt,status.name());
    }

    @Override
    public void deleteAll() {
        String sql = "delete from rentals";

        jdbcTemplate.update(sql);
    }

    @Override
    public int deleteById(Long id) {
        String sql = "delete from rentals where id = ?";
        return jdbcTemplate.update(sql,id);
    }

    @Override
    public List<Rental> findAll() {
        String sql = "select id, user_email, book_item_id, rented_at, due_date,returned_at,status from rentals";

        return jdbcTemplate.query(sql,rentalMapper);
    }

    @Override
    public Rental findById(Long id) {
        try {
            String sql = "select id, user_email, book_item_id, rented_at, due_date,returned_at,status from rentals where id = ?";

            return jdbcTemplate.queryForObject(sql,rentalMapper,id);
        }catch(EmptyResultDataAccessException e){
            throw new EntityNotFoundException("대여 정보를 찾을 수 없습니다.");
        }
    }

    @Override
    public List<Rental> findByUserEmail(String userEmail) {
        String sql = "select id, user_email, book_item_id, rented_at, due_date,returned_at,status from rentals where user_email = ?";
        return jdbcTemplate.query(sql,rentalMapper,userEmail);
    }

    @Override
    public List<Rental> findByBookItemId(Long bookItemId) {
        String sql = "select id, user_email, book_item_id, rented_at, due_date,returned_at,status from rentals where book_item_id = ?";
        return jdbcTemplate.query(sql,rentalMapper,bookItemId);
    }

    @Override
    public Rental findActiveRentalByBookItemId(Long bookItemId) {
        try{
            String sql = "select id, user_email, book_item_id, rented_at, due_date,returned_at,status from rentals where book_item_id = ? and (status = 'RENTED' or status ='OVERDUE')";
            return jdbcTemplate.queryForObject(sql,rentalMapper,bookItemId);
        }catch(EmptyResultDataAccessException e){
            throw new EntityNotFoundException("대여 중인 도서의 대여 정보를 찾을 수 없습니다.");
        }
    }

    @Override
    public List<Rental> findActiveRentalsByUserEmail(String userEmail) {
        String sql = "select id, user_email, book_item_id, rented_at, due_date,returned_at,status from rentals where user_email = ? and (status ='RENTED' or status='OVERDUE')";
        return jdbcTemplate.query(sql,rentalMapper,userEmail);
    }

    @Override
    public int updateReturnedDate(Long id, LocalDate returnedDate) {
        String sql = "update rentals set returned_at = ? , status = 'RETURNED' where id = ? and (status = 'RENTED' or status='OVERDUE')";

        return jdbcTemplate.update(sql,returnedDate,id);
    }

    @Override
    public int getCount() {
            String sql = "select count(*) from rentals";
        Integer count = jdbcTemplate.queryForObject(sql,Integer.class);

        return count!=null?count:0;
    }

    @Override
    public int updateOverdue(LocalDate today){
        String sql = "update rentals set status = 'OVERDUE' where status = 'RENTED' and due_date < ?";

        return jdbcTemplate.update(sql,today);
    }

    @Override
    public boolean existsActiveRentalByUserEmailAndIsbn(String userEmail, String isbn) {
        String sql = "select exists ( select 1 from rentals r join book_items bi on r.book_item_id = bi.id where r.user_email = ? and bi.isbn = ? and (r.status = 'RENTED' or r.status = 'OVERDUE'))";
        Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class, userEmail, isbn);

        return Boolean.TRUE.equals(exists);
    }
}
