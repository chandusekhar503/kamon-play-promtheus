package dao.impl;

import com.google.inject.ImplementedBy;
import model.User;

import java.util.concurrent.CompletableFuture;

/**
 * Created by chandu on 10/02/19.
 */
@ImplementedBy(UserDaoImpl.class)
public interface UserDao {

    public CompletableFuture<Boolean> saveUserDao(User user);
    public CompletableFuture<User> getUserDao(String mobileNumber);
    public CompletableFuture<Boolean> deleteUserDao(String mobileNumber);
}
