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

    private List<Book> findByColumnName(String colName, String keyword, int limit, int offset){
        String searchKeyword = "%"+keyword+"%";
        String sql = "select * from books where "+colName+" like ? limit ? offset ?";

        return this.jdbcTemplate.query(sql,bookMapper,searchKeyword,limit,offset);
    }

    @Override
    public List<Book> findByTitle(String title, int limit, int offset) {
        return findByColumnName("title",title,limit,offset);
    }

    @Override
    public List<Book> findByAuthor(String author, int limit, int offset){
        return findByColumnName("author", author,limit,offset);
    }

    @Override
    public List<Book> findByGenre(String genre, int limit, int offset){
        return findByColumnName("genre",genre,limit,offset);
    }

    public int getCount() {
        return this.jdbcTemplate.queryForObject("select count(*) from books",Integer.class);
    }

    private int getCountByColumn(String colName, String keyword){
        String searchKeyword = "%"+keyword+"%";
        return this.jdbcTemplate.queryForObject("select count(*) from books where "+colName+" like ?",Integer.class,searchKeyword);
    }

    @Override
    public int getCountByTitle(String title){
        return getCountByColumn("title",title);
    }
    @Override
    public int getCountByAuthor(String author){
        return getCountByColumn("author", author);
    }
    @Override
    public int getCountByGenre(String genre){
        return getCountByColumn("genre",genre);
    }
}
