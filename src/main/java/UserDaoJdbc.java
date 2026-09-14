import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

public class UserDaoJdbc implements UserDao {
    private JdbcTemplate jdbcTemplate;

    @Override
    public void setDataSource(DataSource datasource) {
        this.jdbcTemplate = new JdbcTemplate(datasource);
    }

    private RowMapper<User> userMapper = (rs, rowNum) ->{
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));

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
            return null;
        }

    }

    @Override
    public void add(User user,String hashedPassword) {
        this.jdbcTemplate.update("insert into users(id,password,name) values(?,?,?)",user.getId(),hashedPassword,user.getName());
    }

    @Override
    public boolean isExist(String id) {
        Integer count = this.jdbcTemplate.queryForObject("select count(*) from users where id = ?",Integer.class,id);

        return count!=null && count >0 ;
    }
}
