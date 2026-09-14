import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDaoJdbc implements UserDao {
    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;

    private RowMapper<User> userMapper = (rs, rowNum) ->{
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));

        return user;
    };

    @Override
    public void setDataSource(DataSource datasource) {
        this.jdbcTemplate = new JdbcTemplate(datasource);
        //this.dataSource = datasource;
    }

    @Override
    public void deleteAll() {
        this.jdbcTemplate.update("delete from users");
    }

    @Override
    public void signup(User user) throws DuplicateKeyException{
        this.jdbcTemplate.update("insert into users(id,name,password) values(?,?,?)", user.getId(),user.getName(),user.getPassword());
    }

    @Override
    public User login(String id, String password) {
        try{
            return this.jdbcTemplate.queryForObject("select * from users where id=? and password=?",userMapper,id,password);
        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }
}
