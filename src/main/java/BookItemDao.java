import javax.sql.DataSource;
import java.util.List;

public interface BookItemDao {
    public void setDataSource(DataSource dataSource);
    public void add(Book book);
    public void remove(String id);
    public int rent(String id);
}
