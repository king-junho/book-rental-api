import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookService {

        private BookDao bookDao;
        private BookItemDao bookItemDao;

        public void setBookDao(BookDao bookDao) {
            this.bookDao = bookDao;
        }
        public void setBookItemDao(BookItemDao bookItemDao) {this.bookItemDao = bookItemDao;}

        //Book 전체 삭제(Book_item도 같이 삭제)
        public void deleteAll() {
            bookDao.deleteAll();
        }

        //Book ISBN으로 조회
        public Book findBookByISBN(String ISBN){
            return bookDao.findByISBN(ISBN);
        }

        public PageResult<Book> findBookBySearchType(SearchType type, String keyword, int currentPage){
            int searchCount = bookDao.getCountBySearchType(type, keyword);
            Page pageInfo = new Page(searchCount,currentPage);
            List<Book> books = bookDao.findBySearchType(type,keyword, pageInfo.getPageSize(),pageInfo.getStartIndex());

            return new PageResult<>(pageInfo,books);
        }

        public void rentBookItem(String bookItemId){
            int updatedRow = bookItemDao.rent(bookItemId);

            if(updatedRow==0){
                throw new IllegalArgumentException("이미 대여 중이거나 존재하지 않는 도서입니다.");
            }

            //Rental에 기록 남기기
        }

        public void removeBookByISBN(String ISBN){
            //book_items들도 다 사라져야 함 (db 자동 삭제) -> 테스트 필요
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
            //새로 추가되는 책 수만큼 for문 반복, 대여 안 되는 책(있으면) 입고도 추후 고려

            Book targetBook = bookDao.findByISBN(book.getISBN());

            if(targetBook != null){
                targetBook.addQuantity(book.getTotalQuantity());
                bookDao.updateQuantity(targetBook);
            }else{
                bookDao.add(book);
            }
            bookItemDao.add(book);
        }
}

enum SearchType{
    TITLE,
    AUTHOR,
    GENRE,
}


