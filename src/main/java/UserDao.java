import java.sql.*;

public class UserDao {

    public void deleteAll(){
        Connection c = null;
        PreparedStatement ps = null;
        String url = "jdbc:mysql://localhost:3306/book_rental";
        String dbUser = "root";
        String pw = "1994";

        try{
            c = DriverManager.getConnection(url,dbUser,pw);
            ps = c.prepareStatement("delete from users");

            ps.executeUpdate();
        }catch(SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        finally {
            if(ps!=null){
                try{
                    ps.close();
                }catch(SQLException e){}
            }
            if(c!=null){
                try{
                    c.close();
                }catch(SQLException e){}
            }
        }
    }
    //DB 연결
    public void signup(User user){
        Connection c = null;
        PreparedStatement ps = null;
        String url = "jdbc:mysql://localhost:3306/book_rental";
        String dbUser = "root";
        String pw = "1994";

        try{
            c = DriverManager.getConnection(url,dbUser,pw);
            ps = c.prepareStatement("insert into users(id,password,name) values(?,?,?)");
            ps.setString(1,user.getId());
            ps.setString(2,user.getPassword());
            ps.setString(3,user.getName());

            ps.executeUpdate();
        }catch(SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        finally {
            if(ps!=null){
                try{
                    ps.close();
                }catch(SQLException e){}
            }
            if(c!=null){
                try{
                    c.close();
                }catch(SQLException e){}
            }
        }

    }
    public User login(String id, String password){
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String url = "jdbc:mysql://localhost:3306/book_rental";
        String dbUser = "root";
        String pw = "1994";

        try{
            c = DriverManager.getConnection(url, dbUser, pw);
            ps = c.prepareStatement("select * from users where id = ? and password = ?");
            ps.setString(1, id);
            ps.setString(2, password);

            rs = ps.executeQuery();

            if(rs.next()){
                System.out.print(rs.getString("id"));
                return new User(rs.getString("id"),rs.getString("password"),rs.getString("name"));
            }else{
                System.out.println("Id 또는 password 불일치");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }finally {
            if(rs!=null){
                try{
                    rs.close();
                }catch(SQLException e){}
            }
            if(ps!=null){
                try{
                    ps.close();
                }catch(SQLException e){}
            }
            if(c!=null){
                try{
                    c.close();
                }catch(SQLException e){}
            }
        }
        return null;
    }

}
