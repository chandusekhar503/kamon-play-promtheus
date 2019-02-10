package service;


import com.google.inject.ImplementedBy;
import dto.UserDto;
import play.mvc.Result;
import service.impl.UserServiceImpl;

import java.util.concurrent.CompletableFuture;

/**
 * Created by chandu on 10/02/19.
 */
@ImplementedBy(UserServiceImpl.class)
public interface UserService {

    public CompletableFuture<Result> saveUserService(UserDto userDto);
    public CompletableFuture<Result> getUserService(String mobile);
    public CompletableFuture<Result> deleteUserService(String mobile);

}
