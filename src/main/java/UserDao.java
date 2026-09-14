import com.mysql.cj.x.protobuf.MysqlxPrepare;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.*;

public interface UserDao{
    public void setDataSource(DataSource datasource);
    public void deleteAll();
    public void signup(User user);
    public User login(String id, String password);
}
