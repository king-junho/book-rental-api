    import javax.swing.tree.RowMapper;
    import java.util.List;

    public class BookService {

        private BookDao bookDao;

        public void setBookDao(BookDao bookDao) {
            this.bookDao = bookDao;
        }

        public void deleteAll() {
            bookDao.deleteAll();
        }

        public void rentBookByISBN(String ISBN){
            Book targetBook = bookDao.findByISBN(ISBN);

            if(targetBook != null){
                targetBook.rentBook();

                bookDao.updateQuantity(targetBook);
            }else{
                throw new IllegalArgumentException("조회하신 책이 없습니다.");
            }
        }

        public boolean isRentBook(String ISBN){
            Book targetBook =  bookDao.findByISBN(ISBN);
            if(targetBook!= null){
                return targetBook.getAvailableQuantity()>0;
            }else{
                return false;
            }
        }

        public void removeBookByISBN(String ISBN){
            Book targetBook = bookDao.findByISBN(ISBN);

            if(targetBook != null){
                targetBook.removeBook();
                if(targetBook.getTotalQuantity()>0){
                    bookDao.updateQuantity(targetBook);
                }else{
                    bookDao.deleteById(targetBook.getISBN());
                }
            }else{
                throw new IllegalArgumentException("조회하신 책이 없습니다.");
            }
        }
        public void addBook(Book book){
            Book targetBook = bookDao.findByISBN(book.getISBN());

            if(targetBook != null){
                targetBook.addQuantity(book.getTotalQuantity());

                bookDao.updateQuantity(targetBook);
            }else{
                bookDao.add(book);
            }
        }

        public Book findBookByISBN(String ISBN){
            return bookDao.findByISBN(ISBN);
        }

        public List<Book> findBookByTitle(String title) {
            return bookDao.findByTitle(title);
        }

        public List<Book> findBookByAuthor(String author) {
            return bookDao.findByAuthor(author);
        }

        public List<Book> findBookByGenre(String genre) {
            return bookDao.findByGenre(genre);
        }
    }
