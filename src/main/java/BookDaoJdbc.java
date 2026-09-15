import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class BookDaoJdbc implements BookDao {
    JdbcTemplate jdbcTemplate;

    @Override
    public void setDataSource(DataSource datasource) {
        this.jdbcTemplate = new JdbcTemplate(datasource);
    }

    private RowMapper<Book> bookMapper = (rs, rowNum)->{
        Book book = new Book(rs.getString("ISBN"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setGenre(rs.getString("genre"));
        book.setTotalQuantity(rs.getInt("totalQuantity"));
        book.setAvailableQuantity(rs.getInt("availableQuantity"));

        return book;
    };
    @Override
    public void deleteAll() {
        this.jdbcTemplate.execute("delete from books");
    }

    @Override
    public void deleteById(String ISBN) {
        this.jdbcTemplate.update("delete from books where ISBN = ?",ISBN);
    }

    @Override
    public void add(Book book) {
        this.jdbcTemplate.update("insert into books(ISBN,title,author,genre,totalQuantity,availableQuantity) values(?,?,?,?,?,?)",book.getISBN(),book.getTitle(),book.getAuthor(),book.getGenre(),book.getTotalQuantity(),book.getAvailableQuantity());
    }

    @Override
    public boolean isExist(String ISBN) {
        Integer count = this.jdbcTemplate.queryForObject("select count(*) from books where ISBN=?",Integer.class,ISBN);

        return count!=null && count>0;
    }

    @Override
    public Book findByISBN(String ISBN) {
        try{
            return this.jdbcTemplate.queryForObject("select * from books where ISBN=?",bookMapper,ISBN);
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    @Override
    public void updateQuantity(Book book) {
        this.jdbcTemplate.update("update books set totalQuantity=?, availableQuantity=? where ISBN=?"
        ,book.getTotalQuantity(),book.getAvailableQuantity(),book.getISBN());
    }

    private List<Book> findBooksByColumn(String columnName, String keyword){
        String searchKeyword = "%"+keyword+"%";

        String sql = "select * from books where "+columnName+" like ? ";
        return this.jdbcTemplate.query(sql,bookMapper,searchKeyword);
    }


    @Override
    public List<Book> findByTitle(String title){
        return findBooksByColumn("title",title);
    }

    @Override
    public List<Book> findByAuthor(String author){
        return findBooksByColumn("author", author);
    }

    @Override
    public List<Book> findByGenre(String genre){
        return findBooksByColumn("genre", genre);
    }


    public int getCount(){
        return this.jdbcTemplate.queryForObject("select count(*) from books",Integer.class);
    }
}
