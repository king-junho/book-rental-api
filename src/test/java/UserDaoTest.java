public class UserDaoTest {
    public UserDao userDao;

    public static void main(String[] args) {
        UserDao userDao = new UserDao();
        //userDao.signup("junho0873@gmail.com","1994","김준호");


        userDao.login("junho0873@gmail.com","1994");
    }
}
