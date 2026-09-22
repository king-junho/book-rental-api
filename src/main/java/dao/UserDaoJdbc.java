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
    private RowMapper<User> userMapper = (rs, rowNum) ->{
        String id = rs.getString("id");
        String password = rs.getString("password");
        String name = rs.getString("name");

        User user = new User(id,password,name);

        return user;
    };

    @Override
    public void deleteAll() {
        this.jdbcTemplate.update("delete from users");
    }

    @Override
    public User findById(String id) {
        try{
            return this.jdbcTemplate.queryForObject("select * from users where id = ?",userMapper,id);
        }catch(EmptyResultDataAccessException e){
            throw new EntityNotFoundException("해당 유저를 찾을 수 없습니다. id : "+id);
        }

    }

    @Override
    public void add(User user, String hashedPassword) {
        this.jdbcTemplate.update("insert into users(id,password,name) values(?,?,?)",user.getEmail(),hashedPassword,user.getName());
    }

    @Override
    public boolean isExist(String id) {
        String sql = "select exists(select 1 from users where id = ?)";
        Boolean exists = this.jdbcTemplate.queryForObject(sql, Boolean.class,id);

        return Boolean.TRUE.equals(exists);
    }
}
