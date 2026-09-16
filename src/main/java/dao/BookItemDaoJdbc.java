package dao;

import domain.Book;
import domain.BookItem;
import domain.enums.BookStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class BookItemDaoJdbc implements BookItemDao{
    private final JdbcTemplate jdbcTemplate;

    public BookItemDaoJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<BookItem> bookItemRowMapper = (rs, rowNum)->{
        BookItem bookItem = new BookItem(rs.getLong("id"), rs.getString("ISBN"));
        bookItem.setStatus(BookStatus.valueOf(rs.getString("status")));

        return bookItem;
    };

    @Override
    public void add(Book book) {
        String sql = "insert into book_items(ISBN,status) values(?,?)";
        //int totalCount = book.getTotalQuantity();

//        for(int i=0; i<totalCount; i++){
//            this.jdbcTemplate.update(sql,book.getISBN(), BookStatus.AVAILABLE.name());
//        }
    }

    @Override
    public void remove(String id) {
        String sql = "delete from book_items where id=?";
        this.jdbcTemplate.update(sql,id);
    }

    @Override
    public int rent(Long id) {
        String sql = "update book_items set status=? where id=? and status = ?";
        return this.jdbcTemplate.update(sql, BookStatus.RENTED.name(),id, BookStatus.AVAILABLE.name());
    }
}
