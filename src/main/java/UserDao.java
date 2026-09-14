import com.mysql.cj.x.protobuf.MysqlxPrepare;

import java.sql.*;

public class UserDao {
    private ConnectionMaker connectionMaker;

    public UserDao(ConnectionMaker connectionMaker){
        this.connectionMaker = connectionMaker;
    }

    public void deleteAll(){
        String sql = "delete from users";

        try(Connection c= connectionMaker.makeConnection();
        PreparedStatement ps = c.prepareStatement(sql)){
            ps.executeUpdate();
        }catch(SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void signup(User user){
        String sql = "insert into users(id,password,name) values(?,?,?)";

        try(Connection c = connectionMaker.makeConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setString(1, user.getId());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getName());

            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    public User login(String id, String password){
        String sql = "select * from users where id=? and password=?";

        try(Connection c = connectionMaker.makeConnection();
        PreparedStatement ps = c.prepareStatement(sql);){
            ps.setString(1, id);
            ps.setString(2, password);

            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    System.out.println(id);
                    return new User(rs.getString("id"),rs.getString("password"),rs.getString("name"));
                }
            }catch (SQLException e){
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }
}
