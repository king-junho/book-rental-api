import javax.sql.DataSource;

public interface UserDao{
    public void setDataSource(DataSource datasource);
    public void deleteAll();
    public User findById(String id);
    public void add(User user,String hashedPassword);
    public boolean isExist(String id);
}
