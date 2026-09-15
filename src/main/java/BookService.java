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

        public PageResult<Book> findBookByTitle(String title, int currentPage) {
            int searchCount = bookDao.getCountByTitle(title);
            Page pageInfo = new Page(searchCount,currentPage);
            List<Book> books = bookDao.findByTitle(title,pageInfo.getPageSize(),pageInfo.getStartIndex());

            return new PageResult<>(pageInfo,books);
        }

        public PageResult<Book> findBookByAuthor(String author, int currentPage) {
            int searchCount = bookDao.getCountByAuthor(author);
            Page pageInfo = new Page(searchCount, currentPage);
            List<Book> books = bookDao.findByAuthor(author, pageInfo.getPageSize(),pageInfo.getStartIndex());


            return new PageResult<>(pageInfo,books);
        }

        public PageResult<Book> findBookByGenre(String genre, int currentPage) {
            int searchCount = bookDao.getCountByGenre(genre);
            Page pageInfo = new Page(searchCount,currentPage);
            List<Book> books = bookDao.findByGenre(genre, pageInfo.getPageSize(),pageInfo.getStartIndex());

            return new PageResult<>(pageInfo,books);
        }
    }
