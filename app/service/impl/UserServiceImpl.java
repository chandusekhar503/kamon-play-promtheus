package service.impl;

import dao.impl.UserDao;
import dto.UserDto;
import model.User;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import service.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;

/**
 * Created by chandu on 10/02/19.
 */
@Singleton
public class UserServiceImpl implements UserService {

    @Inject
    UserDao userDao;

    @Override
    public CompletableFuture<Result> saveUserService(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setMobile(userDto.getMobile());
        user.setName(userDto.getName());

        return userDao.saveUserDao(user).thenApply(response -> {
            return Results.ok(Json.toJson(response));
        });
    }

    @Override
    public CompletableFuture<Result> getUserService(String mobile) {
        return userDao.getUserDao(mobile).thenApply(response -> {
            if (response != null)
                return Results.ok(Json.toJson(response));
            else
                return Results.ok("No data found");
        });
    }

    @Override
    public CompletableFuture<Result> deleteUserService(String mobile) {
        return userDao.deleteUserDao(mobile).thenApply(response -> {
            if (response != null && response == true)
                return Results.ok(Json.toJson(response));
            else
                return Results.ok("No data found");
        });
    }
}
