package dao;

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
    public List<Book> findBySearchType(SearchType type, String keyword, int limit, int offset) {
        String searchKeyword = "%"+keyword+"%";
        String colName = getColumnName(type);
        String sql = "select isbn,title,author,genre from books where "+colName+" like ? order by isbn limit ? offset ?";

        return this.jdbcTemplate.query(sql,bookMapper,searchKeyword,limit,offset);
    }

    @Override
    public int getCountBySearchType(SearchType type, String keyword){
        String searchKeyword = "%"+keyword+"%";
        String colName = getColumnName(type);
        String sql = "select count(*) from books where "+colName+" like ?";

        return this.jdbcTemplate.queryForObject(sql ,Integer.class,searchKeyword);
    }
}
