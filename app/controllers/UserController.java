package controllers;

import dto.UserDto;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import service.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;

/**
 * Created by chandu on 10/02/19.
 */
@Singleton
public class UserController extends Controller {


    @Inject
    FormFactory formFactory;

    @Inject
    UserService userService;

    public CompletableFuture<Result> saveUser() {
        Form<UserDto> form = formFactory.form(UserDto.class).bindFromRequest();
        UserDto userDto = form.get();
        return userService.saveUserService(userDto);
    }

    public CompletableFuture<Result> getUser() {
        String mobileNumber = request().getHeader("X-MOBILE");
        return userService.getUserService(mobileNumber);
    }

    public CompletableFuture<Result> deleteUser() {
        String mobileNumber = request().getHeader("X-MOBILE");
        return userService.deleteUserService(mobileNumber);
    }


}
