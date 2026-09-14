import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlConnectionMaker implements ConnectionMaker {

    @Override
    public Connection makeConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/book_rental";
        String dbUser = "root";
        String pw = "1994";

        Connection c = DriverManager.getConnection(url,dbUser,pw);
        return c;
    }
}
