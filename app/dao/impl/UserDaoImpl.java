package dao.impl;

import com.google.inject.Inject;
import model.User;
import startup.SystemMetricsInitializer;

import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by chandu on 10/02/19.
 */
@Singleton
public class UserDaoImpl implements UserDao {

    @Inject
    SystemMetricsInitializer systemMetricsInitializer;


    @Override
    public CompletableFuture<Boolean> saveUserDao(User user) {
        return CompletableFuture.supplyAsync(() -> {
            user.save();
            return true;
        });
    }

    @Override
    public CompletableFuture<User> getUserDao(String mobileNumber) {
        return CompletableFuture.supplyAsync(() -> {
            List<User> userList = User.find.where().eq("mobile", mobileNumber).findList();
            if (userList != null && !userList.isEmpty())
                return userList.get(0);
            else
                return null;
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteUserDao(String mobileNumber) {
        return CompletableFuture.supplyAsync(() -> {
            List<User> userList = User.find.where().eq("mobile", mobileNumber).findList();
            if (userList != null && !userList.isEmpty()){
                userList.get(0).delete();
                return true;
            }else
                return false;
        });
    }
}
