package dao;

import domain.BookItem;
import domain.enums.BookItemStatus;
import exception.EntityNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class BookItemDaoJdbc implements BookItemDao{
    private final JdbcTemplate jdbcTemplate;

    public BookItemDaoJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<BookItem> bookItemMapper = (rs, rowNum)->{
        Long id = rs.getLong("id");
        String isbn = rs.getString("isbn");
        BookItemStatus status = BookItemStatus.valueOf(rs.getString("status"));

        return BookItem.restore(id,isbn,status);
    };

    @Override
    public void add(String isbn) {
        String sql = "insert into book_items (isbn) values (?)";
        jdbcTemplate.update(sql,isbn);
    }

    @Override
    public void addBatch(String isbn, int count) {
        String sql = "insert into book_items (isbn) values (?)";
        jdbcTemplate.batchUpdate(sql,new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1,isbn);
            }

            @Override
            public int getBatchSize() {
                return count;
            }
        });
    }

    @Override
    public void deleteAll() {
        String sql = "delete from book_items";
        jdbcTemplate.update(sql);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "delete from book_items where id = ?";
        jdbcTemplate.update(sql,id);
    }

    @Override
    public BookItem findById(Long id) {
        try{
            String sql = "select id,isbn,status from book_items where id = ?";
            BookItem bookItem = jdbcTemplate.queryForObject(sql,bookItemMapper,id);

            return bookItem;
        }catch(EmptyResultDataAccessException e){
            throw new EntityNotFoundException("해당 책(아이템)을 찾을 수 없습니다. id : "+id);
        }
    }

    @Override
    public List<BookItem> findByIsbn(String isbn) {
        String sql = "select id,isbn,status from book_items where isbn = ?";

        return jdbcTemplate.query(sql,bookItemMapper,isbn);
    }

    @Override
    public int rent(Long id) {
        String sql = "update book_items set status = 'RENTED' where id=? and status = 'AVAILABLE'";
        return jdbcTemplate.update(sql,id);
    }

    @Override
    public int returnBook(Long id) {
        String sql = "update book_items set status = 'AVAILABLE' where id = ? and status = 'RENTED'";
        return jdbcTemplate.update(sql,id);
    }

    @Override
    public int getAvailableBookCount(String isbn) {
        String sql = "select count(*) from book_items where isbn = ? and status = 'AVAILABLE'";
        Integer count = jdbcTemplate.queryForObject(sql,Integer.class,isbn);

        return count!=null?count:0;
    }

    @Override
    public int getBookCount(String isbn){
        String sql = "select count(*) from book_items where isbn = ?";
        Integer count = jdbcTemplate.queryForObject(sql,Integer.class,isbn);

        return count!=null?count:0;
    }

    @Override
    public List<BookItem> findAvailableBooks(String isbn) {
        String sql = "select id,isbn,status from book_items where isbn = ? and status = 'AVAILABLE'";
        return jdbcTemplate.query(sql,bookItemMapper,isbn);
    }
}
