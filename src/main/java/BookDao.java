import javax.sql.DataSource;
import java.awt.print.Pageable;
import java.util.List;

public interface BookDao {
    public void setDataSource(DataSource datasource);
    public void deleteAll();
    public void add(Book book);
    public boolean isExist(String id);
    public Book findByISBN(String isbn);
    public void updateQuantity(Book book);
    public void deleteById(String isbn);
    public List<Book> findByTitle(String title);
    public List<Book> findByAuthor(String author);
    public List<Book> findByGenre(String genre);
}
