package dao;

import dao.model.BookSearchRow;
import domain.Book;
import domain.enums.SearchType;
import exception.EntityNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import java.util.List;

public class BookDaoJdbc implements BookDao {
    private final JdbcTemplate jdbcTemplate;

    public BookDaoJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Book> bookMapper = (rs, rowNum)->{
        String isbn = rs.getString("isbn");
        String title = rs.getString("title");
        String author = rs.getString("author");
        String genre = rs.getString("genre");

        return new Book(isbn,title,author,genre);
    };

    private final RowMapper<BookSearchRow> bookSearchMapper =(rs,rowNum) ->{
        Book book = new Book(rs.getString("isbn"),rs.getString("title"),rs.getString("author"),rs.getString("genre"));

        int totalCount = rs.getInt("total_count");
        int availableCount = rs.getInt("available_count");

        return new BookSearchRow(book,totalCount,availableCount);
    };

    @Override
    public void deleteAll() {
        this.jdbcTemplate.execute("delete from books");
    }

    @Override
    public void deleteByIsbn(String isbn) {
        this.jdbcTemplate.update("delete from books where isbn = ?",isbn);
    }

    @Override
    public void add(Book book) {
        this.jdbcTemplate.update("insert into books(isbn,title,author,genre) values(?,?,?,?)",book.getIsbn(),book.getTitle(),book.getAuthor(),book.getGenre());
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        String sql = "select exists(select 1 from books where isbn = ?)";
        Boolean exists = this.jdbcTemplate.queryForObject(sql, Boolean.class,isbn);

        return Boolean.TRUE.equals(exists);
    }

    @Override
    public Book findByIsbn(String isbn){
        try{
            return this.jdbcTemplate.queryForObject("select isbn,title,author,genre from books where isbn=?",bookMapper,isbn);
        }catch(EmptyResultDataAccessException e){
            throw new EntityNotFoundException("해당 책을 찾을 수 없습니다. Isbn : "+isbn);
        }
    }

    private String getColumnName(SearchType type){
        switch(type){
            case TITLE:return "title";
            case AUTHOR:return "author";
            case GENRE:return "genre";
            default : throw new IllegalArgumentException("지원하지 않는 검색 조건입니다.");
        }
    }

    @Override
    public List<BookSearchRow> findDetailsBySearchType(SearchType type, String keyword, int limit, int offset) {
        String searchKeyword = "%"+keyword+"%";
        String colName = getColumnName(type);
        String sql =
                """
                select b.isbn, b.title, b.author, b.genre, count(bi.id) as total_count,
                sum( case when bi.status = 'AVAILABLE' then 1 else 0 end) as available_count
                from books b left join book_items bi on b.isbn = bi.isbn
                where b.%s like ? group by b.isbn, b.title, b.author, b.genre order by b.isbn limit ? offset ?
                """.formatted(colName);

        return this.jdbcTemplate.query(sql,bookSearchMapper,searchKeyword,limit,offset);
    }

    @Override
    public int getCountBySearchType(SearchType type, String keyword){
        String searchKeyword = "%"+keyword+"%";
        String colName = getColumnName(type);
        String sql = "select count(*) from books where "+colName+" like ?";

        return this.jdbcTemplate.queryForObject(sql ,Integer.class,searchKeyword);
    }
}
