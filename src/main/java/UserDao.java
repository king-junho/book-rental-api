import java.sql.*;

public class UserDao {
    //DB 연결

    public void signup(String email, String password, String name){
        Connection c = null;
        PreparedStatement ps = null;
        String url = "jdbc:mysql://localhost:3306/book_rental";
        String user = "root";
        String pw = "1994";

        User user1 = new User();
        user1.setId(email);
        user1.setPassword(password);
        user1.setName(name);

        try{
            c = DriverManager.getConnection(url,user,pw);
            ps = c.prepareStatement("insert into users(email,password,name) values(?,?,?)");
            ps.setString(1,email);
            ps.setString(2,password);
            ps.setString(3,name);

            ps.executeUpdate();
        }catch(SQLException e){
            System.out.println(e);
        }
        finally {

        }

    }
    public void login(String email, String password){
        Connection c = null;
        PreparedStatement ps = null;
        String url = "jdbc:mysql://localhost:3306/book_rental";
        String user = "root";
        String pw = "1994";

        try{
            c = DriverManager.getConnection(url, user, pw);
            ps = c.prepareStatement("select * from users where email = ? and password = ?");
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            rs.next();

            if(rs!=null){
                System.out.print(rs.getString("email"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
