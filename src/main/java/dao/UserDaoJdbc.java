package dao;

import domain.User;
import exception.EntityNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class UserDaoJdbc implements UserDao {
    private final JdbcTemplate jdbcTemplate;

    public UserDaoJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userMapper = (rs, rowNum) ->{
        String email = rs.getString("email");
        String password = rs.getString("encoded_password");
        String name = rs.getString("name");

        return new User(email,password,name);
    };

    @Override
    public void deleteAll() {
        this.jdbcTemplate.update("delete from users");
    }

    @Override
    public User findByEmail(String email) {
        try{
            return this.jdbcTemplate.queryForObject("select email,encoded_password,name from users where email = ?",userMapper,email);
        }catch(EmptyResultDataAccessException e){
            throw new EntityNotFoundException("해당 유저를 찾을 수 없습니다. email : "+email);
        }

    }

    @Override
    public void add(User user) {
        this.jdbcTemplate.update("insert into users(email,encoded_password,name) values(?,?,?)",user.getEmail(),user.getEncodedPassword(),user.getName());
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "select exists(select 1 from users where email = ?)";
        Boolean exists = this.jdbcTemplate.queryForObject(sql, Boolean.class,email);

        return Boolean.TRUE.equals(exists);
    }
}
