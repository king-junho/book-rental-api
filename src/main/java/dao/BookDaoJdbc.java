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

    private RowMapper<Book> bookMapper = (rs, rowNum)->{
        String ISBN = rs.getString("ISBN");
        String title = rs.getString("title");
        String author = rs.getString("author");
        String genre = rs.getString("genre");

        Book book = new Book(ISBN,title,author,genre);

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
        this.jdbcTemplate.update("insert into books(ISBN,title,author,genre) values(?,?,?,?)",book.getISBN(),book.getTitle(),book.getAuthor(),book.getGenre());
    }

    @Override
    public boolean isExist(String id) {
        String sql = "select exists(select 1 from books where ISBN = ?)";
        Boolean exists = this.jdbcTemplate.queryForObject(sql, Boolean.class,id);

        return Boolean.TRUE.equals(exists);
    }

    @Override
    public Book findById(String id) {
        try{
            return this.jdbcTemplate.queryForObject("select * from books where ISBN=?",bookMapper,id);
        }catch(EmptyResultDataAccessException e){
            throw new EntityNotFoundException("해당 책을 찾을 수 없습니다. ISBN : "+id);
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
        String sql = "select * from books where "+colName+" like ? limit ? offset ?";

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
