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

    private RowMapper<Rental> rentalMapper = (rs, rowNum)->{
        Long id = rs.getLong("id");
        String userId = rs.getString("userId");
        Long bookItemId = rs.getLong("bookItemId");
        LocalDate rentedAt = rs.getObject("rentedAt",LocalDate.class);
        LocalDate returnedAt = rs.getObject("returnedAt",LocalDate.class);
        RentalStatus status = RentalStatus.valueOf(rs.getString("status"));

        Rental rental = new Rental(id,userId,bookItemId,rentedAt,returnedAt, status);

        return rental;
    };

    @Override
    public void add(Rental rental) {
        String sql = "insert into rentals(userId, bookItemId, rentedAt, dueDate, returnedAt,status) values(?,?,?,?,?,?)";

        String userId = rental.getUserId();
        Long bookItemId = rental.getBookItemId();
        LocalDate rentedAt = rental.getRentedAt();
        LocalDate dueDate = rental.getDueDate();
        LocalDate returnedAt = rental.getReturnedAt();
        RentalStatus status = rental.getStatus();

        jdbcTemplate.update(sql,userId,bookItemId,rentedAt,dueDate,returnedAt,status.name());
    }

    @Override
    public void deleteAll() {
        String sql = "delete from rentals";

        jdbcTemplate.update(sql);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "delete from rentals where id = ?";
        jdbcTemplate.update(sql,id);
    }

    @Override
    public List<Rental> findAll() {
        String sql = "select * from rentals";
        return jdbcTemplate.query(sql,rentalMapper);
    }

    @Override
    public Rental findById(Long id) {
        try {
            String sql = "select * from rentals where id = ?";
            return jdbcTemplate.queryForObject(sql,rentalMapper,id);
        }catch(EmptyResultDataAccessException e){
            throw new EntityNotFoundException("대여 정보를 찾을 수 없습니다.");
        }
    }

    @Override
    public List<Rental> findByUserId(String userId) {
        String sql = "select * from rentals where userId = ?";
        return jdbcTemplate.query(sql,rentalMapper,userId);
    }

    @Override
    public List<Rental> findByBookItemId(Long bookItemId) {
        String sql = "select * from rentals where bookItemId = ?";
        return jdbcTemplate.query(sql,rentalMapper,bookItemId);
    }

    @Override
    public Rental findActiveRentalByBookItemId(Long bookItemId) {
        try{
            String sql = "select * from rentals where bookItemId = ? and status = 'RENTED'";
            return jdbcTemplate.queryForObject(sql,rentalMapper,bookItemId);
        }catch(EmptyResultDataAccessException e){
            throw new EntityNotFoundException("대여 중인 도서의 대여 정보를 찾을 수 없습니다.");
        }
    }

    @Override
    public List<Rental> findRentedBookByUserId(String userId) {
        String sql = "select * from rentals where userId = ? and status ='RENTED'";
        return jdbcTemplate.query(sql,rentalMapper,userId);
    }

    @Override
    public void updateReturnedDate(Long id, LocalDate returnedDate) {
        String sql = "update rentals set returnedAt = ? , status = 'RETURNED' where id = ?";
        jdbcTemplate.update(sql,returnedDate,id);
    }

    @Override
    public void updateStatus(Long id, RentalStatus status) {
        String sql ="update rentals set status = ? where id =?";
        jdbcTemplate.update(sql,status.name(),id);
    }

    @Override
    public int getCount() {
        String sql = "select count(*) from rentals";
        Integer count = jdbcTemplate.queryForObject(sql,Integer.class);

        return count!=null?count:0;
    }
}
