package dao;

import org.springframework.jdbc.core.JdbcTemplate;

public class TestDataCleaner {
    private final JdbcTemplate jdbcTemplate;

    public TestDataCleaner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void cleanUp(){
        jdbcTemplate.update("delete from rentals");
        jdbcTemplate.update("delete from book_items");
        jdbcTemplate.update("delete from books");
        jdbcTemplate.update("delete from users");
    }

}
